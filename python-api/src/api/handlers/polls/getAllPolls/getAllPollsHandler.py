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

def getAllPolls(event, context):


    params = {
        'IndexName': 'ListPollsTitles',
        'KeyConditionExpression': 'GSI1PK = :v',
        'ExpressionAttributeValues': {
            ':v': 'POLLS'
        }
    }

    try:
        table = dynamodb_client.Table(TABLE_NAME)
        response = table.query(**params)
    except Exception as ex:
        return create_response(500, str(ex))

    print('response: ', response)

    if response['ResponseMetadata']['HTTPStatusCode'] != 200:
      return create_response(500, 'Error getting the polls')

    polls = response['Items']

    return create_response(200, polls)

