try:
    import unzip_requirements
except ImportError:
    pass

import boto3
import os
import simplejson as json

dynamodb_client = boto3.resource('dynamodb')
TABLE_NAME = os.environ['FORMS_TABLE_NAME']

def create_response(code, message):
  return {
    'statusCode': code,
    'headers': {'Content-Type': 'application/json'},
    'body': json.dumps(message)
  }

def createResult(event, context):

    result = json.loads(event['body'])

    params = {
      'Item': result
    }

    print('params', params)

    try:
      table = dynamodb_client.Table(TABLE_NAME)
      response = table.put_item(**params)
    except Exception as ex:
      print('Ex: ', ex)
      return create_response(500, 'Error creating the Result')

    print('Response', response)

    if response['ResponseMetadata']['HTTPStatusCode'] != 200:
      return create_response(500, 'Error creating the Result')

    return create_response(200, params['Item'])

