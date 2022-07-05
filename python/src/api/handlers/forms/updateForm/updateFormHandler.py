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
        update_expression.append(f" {key} = :{key},")
        update_values[f":{key}"] = val

    return "".join(update_expression)[:-1], update_values

def updateForm(event, context):

    PK = event['pathParameters']['id']
    form = json.loads(event['body'])

    updateExpression, expressionAttributesValues = get_update_params(form)

    # paramName = reqBody['paramName']
    # paramValue = reqBody['paramValue']
    
    params = {
        'Key': {
            'PK': DYNAMO_FORMS_ID + '#' + PK,
            'SK': DYNAMO_FORMS_ID + '#' + PK,
        },
        'ConditionExpression': 'attribute_exists(PK) AND attribute_exists(SK)',
        'UpdateExpression': updateExpression,
        'ExpressionAttributeValues': expressionAttributesValues,
        # 'UpdateExpression': 'set ' + paramName + ' = :v',
        # 'ExpressionAttributeValues': {
        #     ':v': paramValue
        # },
        'ReturnValues': 'ALL_NEW'
    }

    try:
        table = dynamodb_client.Table(TABLE_NAME)
        response = table.update_item(**params)
    except Exception as ex:
        return create_response(500, 'Error updating the Form')

    print('Response', response)

    if response['ResponseMetadata']['HTTPStatusCode'] != 200:
      return create_response(500, 'Error updating the Form')

    response = response['Attributes']

    return create_response(200, response)

