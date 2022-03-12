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

def getPoll(event, context):

    pk = event['pathParameters']['id']
    print('pk: ', pk)

    pk = DYNAMO_POLLS_ID + '#' + pk
    params = {
        'Key': {
            'PK': pk,
            'SK': pk
        }
    }

    try:
        table = dynamodb_client.Table(TABLE_NAME)
        response = table.get_item(**params)
    except Exception as ex:
        return create_response(500, str(ex))

    print('response: ', response)

    if response['ResponseMetadata']['HTTPStatusCode'] != 200:
      return create_response(500, 'Error getting the Poll')

    if not 'Item' in response:
        return create_response(404, 'Poll with id: ' + pk + ' not found')

    poll = response['Item']

    return create_response(200, poll)

