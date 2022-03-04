try:
    import unzip_requirements
except ImportError:
    pass

import boto3
import os
import simplejson as json

dynamodb_client = boto3.resource('dynamodb')
TABLE_NAME = os.environ['POLLS_TABLE_NAME']
DYNAMO_POLLS_ID = os.environ['POLLS_ID']

def create_response(code, message):
  return {
    'statusCode': code,
    'headers': {'Content-Type': 'application/json'},
    'body': json.dumps(message)
  }

def getPollByTitle(event, context):
    title = event['queryStringParameters']['title']

    params = {
        'IndexName': 'SearchPollByTitle',
        'KeyConditionExpression': 'title = :v AND begins_with(PK, :v1)',
        'ExpressionAttributeValues': {
            ':v': title,
            ':v1': 'POLL'
        }
    }

    try:
        table = dynamodb_client.Table(TABLE_NAME)
        response = table.query(**params)
    except Exception as ex:
        return create_response(500, str(ex))

    print('response: ', response)

    if response['ResponseMetadata']['HTTPStatusCode'] != 200:
      return create_response(500, 'Error getting the poll')

    if not response['Items'] or len(response['Items']) != 1:
        return create_response(500, { 'message': 'Poll not found' })

    poll = response['Items'][0]

    return create_response(200, poll)

