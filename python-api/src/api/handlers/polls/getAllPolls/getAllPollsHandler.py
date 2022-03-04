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

    table = dynamodb_client.Table(TABLE_NAME)

    params = {
        'IndexName': 'ListPollsTitles',
        'KeyConditionExpression': 'GSI1PK = :v',
        'ExpressionAttributeValues': {
            ':v': 'POLLS'
        }
    }


    response = table.query(**params)

    print('response: ', response)

    polls = response['Items']

    return create_response(200, polls)

