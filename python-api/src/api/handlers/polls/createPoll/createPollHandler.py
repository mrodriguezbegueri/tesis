try:
    import unzip_requirements
except ImportError:
    pass

import boto3
import os
import simplejson as json
import uuid

dynamodb_client = boto3.resource('dynamodb')
TABLE_NAME = os.environ['POLLS_TABLE_NAME']
DYNAMO_POLLS_ID = os.environ['POLLS_ID']

def create_response(code, message):
  return {
    'statusCode': code,
    'headers': {'Content-Type': 'application/json'},
    'body': json.dumps(message)
  }

def createPoll(event, context):

    request = json.loads(event['body'])
    poll_id = str(uuid.uuid4())

    params = {
      'Item': {
        'PK': DYNAMO_POLLS_ID + '#' + poll_id,
        'SK': DYNAMO_POLLS_ID + '#' + poll_id,
        'GSI1PK': 'POLLS',
        'title': request['title'],
        'description': request['description'],
        'groups': request['groups'],
      }
    }

    print('params', params)

    try:
      table = dynamodb_client.Table(TABLE_NAME)
      response = table.put_item(**params)
    except Exception as ex:
      return create_response(500, str(ex))


    print('Response', response)

    if response['ResponseMetadata']['HTTPStatusCode'] != 200:
      return create_response(500, 'Error creating the poll')

    return create_response(200, params['Item'])

