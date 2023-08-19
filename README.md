# Proxy Reverso
Proxy reverso para mercado libre, esta solución se encarga de lo siguiente:
- Enrrutar por medio de un proxy las peticiones a la api de mercadolibre.
- Generar limites de consumo de peticiones por IP y cantidad de solicitudes por minuto.
- La solución se desplega en un entorno cloud con AWS.
- Las métricas de la aplicación se visualizan en OpenSearch ó ElasticSearch.

# Diagramas de soluciones
- Elastic Beanstalk
 ![Meli-Reverse-Proxy 1.drawio.png](img%2FMeli-Reverse-Proxy%201.drawio.png)

- Fargate
- ![Meli-Reverse-Proxy2.drawio.png](img%2FMeli-Reverse-Proxy2.drawio.png)

# Clases
- ReverseProxyApplication: Clase principal que levanta el contexto de Spring
- ProxyController: Punto de entrada a la aplicación donde se mapean los servicios REST para su filtro a api.mercadolibre.com, adicional se configura los limites de consumo por API.
- ProxyService: Servicio que se encarga de realizar el proxy reverso a la URI que se envia por variable de entorno PROXY-TARGET.

# Pruebas Unitarias
- ReverseProxyApplicationTests: Valida la carga en contexto de memoria de los controladores
- ProxyMockTest: Realiza el redireccionamiento hacia la API de mercado libre api.mercadolibre.com para los módulos de categorias y ventas 
- ProxyLoadTest: Realiza las pruebas de carga con los siguientes sets:
  1.  1000 solicitudes por minuto path /categories/*
  2. 10 solicitudes por minuto path /sell/*