const processIds = (context, event, done) => {

    let resultId = context.vars.savedResult.PK
    let formId = context.vars.savedResult.SK

    context.vars['resultPK'] = resultId.substring(resultId.indexOf('#') + 1)
    context.vars['resultSK'] = formId.substring(formId.indexOf('#') + 1)

    console.log("resultPK", context.vars.resultPK)
    console.log("resultSK", context.vars.resultSK)

    return done()
}

module.exports = {
    processIds
}