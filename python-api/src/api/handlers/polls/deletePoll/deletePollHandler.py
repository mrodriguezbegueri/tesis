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

def deletePoll(event, context):

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
        response = table.delete_item(**params)
    except Exception as ex:
        return create_response(500, str(ex))

    print('response: ', response)

    if response['ResponseMetadata']['HTTPStatusCode'] != 200:
      return create_response(500, 'Error deleting the Poll')

    return create_response(200, 'Poll with id: ' + pk + 'deleted successfully')

