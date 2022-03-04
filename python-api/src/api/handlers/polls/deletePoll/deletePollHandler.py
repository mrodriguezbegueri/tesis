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

    table = dynamodb_client.Table(TABLE_NAME)
    
    params = {
        'Key': {
            'PK': pk,
            'SK': pk
        }
    }

    response = table.delete_item(**params)
    print('response: ', response)

    return create_response(200, { 'message': 'Poll deleted successfully' })

