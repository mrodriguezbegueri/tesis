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

def updatePoll(event, context):

    PK = event['pathParameters']['id']
    reqBody = json.loads(event['body'])
    paramName = reqBody['paramName']
    paramValue = reqBody['paramValue']
    
    params = {
        'Key': {
            'PK': DYNAMO_POLLS_ID + '#' + PK,
            'SK': DYNAMO_POLLS_ID + '#' + PK,
        },
        'ConditionExpression': 'attribute_exists(PK) AND attribute_exists(SK)',
        'UpdateExpression': 'set ' + paramName + ' = :v',
        'ExpressionAttributeValues': {
            ':v': paramValue
        },
        'ReturnValues': 'ALL_NEW'
    }

    table = dynamodb_client.Table(TABLE_NAME)

    response = table.update_item(**params)

    print('Response', response)

    return create_response(200, response)

