try:
    import unzip_requirements
except ImportError:
    pass

import boto3
import os
import simplejson as json

dynamodb_client = boto3.resource('dynamodb')
TABLE_NAME = os.environ['POLLS_TABLE_NAME']
RESULTS_ID = os.environ['RESULTS_ID']

def create_response(code, message):
  return {
    'statusCode': code,
    'headers': {'Content-Type': 'application/json'},
    'body': json.dumps(message)
  }

def get_update_params(body):
    update_expression = ["set "]
    update_values = dict()

    for key, val in body.items():
        if key == 'PK' or key == 'SK':
            continue
        update_expression.append(f" {key} = :{key},")
        update_values[f":{key}"] = val

    return "".join(update_expression)[:-1], update_values

def updateResult(event, context):

    PK = event['pathParameters']['id']
    result = json.loads(event['body'])

    SK = result['SK']

    updateExpression, expressionAttributesValues = get_update_params(result)
    
    params = {
        'Key': {
            'PK': RESULTS_ID + '#' + PK,
            'SK': SK,
        },
        'ConditionExpression': 'attribute_exists(PK) AND attribute_exists(SK)',
        'UpdateExpression': updateExpression,
        'ExpressionAttributeValues': expressionAttributesValues,
        'ReturnValues': 'ALL_NEW'
    }

    try:
        table = dynamodb_client.Table(TABLE_NAME)
        response = table.update_item(**params)
    except Exception as ex:
        print('Ex: ', ex)
        return create_response(500, 'Error updating the Poll')

    print('Response: ', response)

    if response['ResponseMetadata']['HTTPStatusCode'] != 200:
      return create_response(500, 'Error updating the Poll')

    response = response['Attributes']

    return create_response(200, response)
