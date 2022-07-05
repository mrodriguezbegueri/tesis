try:
    import unzip_requirements
except ImportError:
    pass

import boto3
import os
import simplejson as json

dynamodb_client = boto3.resource('dynamodb')
TABLE_NAME = os.environ['FORMS_TABLE_NAME']
DYNAMO_FORMS_ID = os.environ['FORMS_ID']
DYNAMO_RESULTS_ID = os.environ['RESULTS_ID']

def create_response(code, message):
  return {
    'statusCode': code,
    'headers': {'Content-Type': 'application/json'},
    'body': json.dumps(message)
  }

def deleteResult(event, context):

    pk = event['pathParameters']['id']
    pk = DYNAMO_RESULTS_ID + '#' + pk
    print('pk: ', pk)

    sk = event['queryStringParameters']['formId']
    sk = DYNAMO_FORMS_ID + '#' + sk
    print('sk: ', sk)

    params = {
        'Key': {
            'PK': pk,
            'SK': sk
        }
    }

    try:
        table = dynamodb_client.Table(TABLE_NAME)
        response = table.delete_item(**params)
    except Exception as ex:
        return create_response(500, 'Error deleting the Result')

    print('response: ', response)

    if response['ResponseMetadata']['HTTPStatusCode'] != 200:
      return create_response(500, 'Error deleting the Result')

    return create_response(200, 'Result with id: ' + pk + ' deleted successfully')