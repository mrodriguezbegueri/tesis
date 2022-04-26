# Correr Tests

Para correr un test se debe hacer de la siguiente forma:

``
npm run load_test:js --vus=VUS_10

``
donde el parametro "js" sera el lenguaje a ejecutar y "vus" Serán la cantidad de usuarios virtuales a ejecutar en el test, si fueran 50 entonces colocariamos "--vus=VUS_50"

# Generar Reportes

Para generar un reporte de un test ejecutado anteriormente, se debe hacer de la siguiente forma:

``
npm run report:py --vus=VUS_10

``
donde el parametro "py" sera el lenguaje a ejecutar y "vus" Serán la cantidad de usuarios virtuales a ejecutar en el test, si fueran 50 entonces colocariamos "--vus=VUS_50"