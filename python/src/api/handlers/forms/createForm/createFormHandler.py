try:
    import unzip_requirements
except ImportError:
    pass

import boto3
import os
import simplejson as json
import uuid

dynamodb_client = boto3.resource('dynamodb')
TABLE_NAME = os.environ['FORMS_TABLE_NAME']
DYNAMO_FORMS_ID = os.environ['FORMS_ID']

def create_response(code, message):
  return {
    'statusCode': code,
    'headers': {'Content-Type': 'application/json'},
    'body': json.dumps(message)
  }

def createForm(event, context):

    request = json.loads(event['body'])
    form_id = str(uuid.uuid4())

    params = {
      'Item': {
        'PK': DYNAMO_FORMS_ID + '#' + form_id,
        'SK': DYNAMO_FORMS_ID + '#' + form_id,
        'GSI1PK': 'FORMS',
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
      return create_response(500, 'Error creating the Form')


    print('Response', response)

    if response['ResponseMetadata']['HTTPStatusCode'] != 200:
      return create_response(500, 'Error creating the Form')

    return create_response(200, params['Item'])

