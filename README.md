# Bran

O objetivo desse repositório é exemplificar, bem como explicar os pontos mais importantes da arquitetura pensada para o projeto Bran.

## Arquitura software

Conforme pautado nas reuniões anteriores, o Bran possui algumas premissas, dentre elas:

- Cloud first
- Multi-tenancy
- Blockchain
- API oriented

Sendo assim, continuar usando o modelo de desenvolvimento monolítico e single-tenant que estávamos acostumado com a Dataeasy não é algo que atende tais demanadas e por isso estou propondo uma nova abordagem no desenvolvimento do Bran.

Primeiramente e provavelmente a maior mudança é saírmos do padrão monolítico e migrarmos para uma arquiterura de software distribuída, ganhando com isso, entre outras coisas, a capacidade sermos elásticos.

Adotando um arquitetura focada em microserviços sem dúvida impõe novos desafios que atualmente não estamos familiarizados, mas em contrapartida permite que trabalhemos de forma autônoma entre a equipe e possibilite de sejamos escaláveis, evitando dores como hoje acontece com o Easysearch e o Solr.

Com todo esses pontos em mente e considerando o expertise da equipe em Java, tentei utilizar ao máximo as ferramentas e patterns mais battle-tested do mercado visando construir uma arquitetura onde, apesar de inicialmente parecer desnecessariamente complexa, tentasse abordar os pontos mais comuns de um desenvolvimento distribuído.

Os patterns abordados foram:

- Develoment patterns
    - Protoco de comunicação (JSON)
    - [Design de interface (padrões de url para microserviços)](http://niels.nu/blog/2016/microservice-versioning.html)
    - [Centralização de configuração](https://spring.io/guides/gs/centralized-configuration/)
    - [Comunicação por evento entre microserviços (Kafka)](https://cloud.spring.io/spring-cloud-stream/)

- Routing patterns
    - [Service discovery (Eureka)](https://github.com/Netflix/eureka/wiki)
    - [Service routing (Zuul)](https://github.com/Netflix/zuul/wiki)

- Resilience patterns
    - [Client-side load balancing (Ribbon)](https://github.com/Netflix/ribbon)
    - [Circuit breaker (Hystrix)](https://github.com/Netflix/Hystrix/wiki/How-it-Works#CircuitBreaker)
    - [Fallback (Hystrix)](https://github.com/Netflix/Hystrix/wiki/How-To-Use#Fallback) 
    - [Bulkhead (Hystrix)](https://github.com/Netflix/Hystrix/wiki/How-it-Works#Isolation)

- Security patterns [(Oauth2)](http://cloud.spring.io/spring-cloud-security/single/spring-cloud-security.html)
    - Authentication 
    - Authorization
    - Credentials management (JWT)

- Logging/tracing patterns
    - [Log correlation (Spring Cloud Sleuth)](https://cloud.spring.io/spring-cloud-sleuth/single/spring-cloud-sleuth.html)
    - [Log aggregation (Logspout + ELK/Papertrail)](https://github.com/gliderlabs/logspout)
    - [Tracing (Zipkin)](https://github.com/openzipkin/zipkin)

### Overview

A arquitetura proposta utiliza basicamente Spring Cloud + Netflix OSS, tudo em containers Docker:

![Bran Architecture](https://i.imgur.com/3wzDNyy.png)

## Executando o projeto de exemplo

É possível executar o projeto exemplo de duas maneiras:

1- Executar tudo dentro do docker (forma mais simples de ver funcionando, mas difícil de debug/alterar)

2- Executar as dependências arquiteturais (dbs, redis, elk, etc) via Docker e subir os microserviços manualmente


### Executando o projeto de exemplo via Docker-compose

Obs.: Devido as diversas dependências necessárias, ao menos no OSX, foi necessário liberar no mínimo 2 CPUs e 4GB de ram para o Docker conseguir subir todos os containers, e mesmo assim levou em torno de 5min para terminar a inicialização.

Para subir o stack inteiro da aplicação via docker-compose é simples, bastando:

- Buildar as imagens do docker de cada subprojeto, executando na raíz do projeto Bran ``` mvn clean package docker:build ```

- Iniciar os containers do docker, executando:
```
cd docker/common
docker-compose up
```

### Executando o projeto de exemplo com dependências via docker-compose e microserviços manualmente


Apesar de bem mais complexo executar dessa maneira, é consideravelmente mais rápido e flexível. Para isso:

- Iniciar os containers de dependências, executando:
```
cd docker/common
docker-compose -f docker-compose-local.yml up -d
```

- Definir a variável de ambiente ```ENCRYPT_KEY=IMSYMMETRIC```

- Instalar JCE
    - [Baixar os jars](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)
    - Faça um backup dos arquivos local_policy.jar e US_export_policy.jar do diretório $JAVA_HOME/jre/lib/security
    - Substituia os arquivos acima pelos baixados no primeiro passo

- Iniciar os microserviços, 1 a 1 e na ordem especificada, utilizando ```mvn spring-boot:run``` dentro de cada subdiretório
    - eurekasvr
    - configsvr
    - zuulsvr
    - zipkinsvr
    - authentication-service
    - licensing-service
    - organization-service

Obs.: Por padrão, o microserviço configsvr estará apontando para configs vindas de um repositório do [Github](https://github.com/dambros/bran-configs). Caso esteja utilizando os microserviços fora do docker, é necessário utilizar as configs locais, bastando definir o profile ```native``` na hora que iniciar esse servico, ```mvn spring-boot:run -Dspring.profiles.active=native```. Se preferir utilizar as configs no Github para esse cenário, será necessário fazer um proxy das nomenclaturas do docker-compose para localhost no seu arquivo /etc/hosts.

## Acessando a API REST e dados de acesso dos serviços

Toda API do projeto de exemplo pode ser encontrada na coleção do Postman chamada Bran.postman_collection.json que está na raíz do projeto.

### Dados de acesso dos serviços

Kibana:
http://localhost:5601

Zipkin:
http://localhost:9411

Eureka:
http://localhost:8761


Databases:
```
Authentication - localhost:5432
License - localhost:5433
Organization - localhost:5434
````

Credenciais:
```
User: postgres
Password: p0stgr@s
Db: bran
````

Users cadastrados no serviço de autenticação:
```
username: mario.dambros
password: password2
roles: USER, ADMIN

username: murilo.alencar
password: password1
roles: USER

client: bran
secret: thisissecret
scopes: webclient, mobileclient
```

## Configurando Kibana

Basta definir o index pattern como ```logstash-*```. Com isso é possível buscar todos os logs na aba Discover. 

## Testes

Durante o desenvolvimento desse projeto de exemplo, ignorei completamente a parte de testes. Isso não quer dizer que faremos o mesmo no desenvolvimento da aplicação.

Fica a critério de cada desenvolvedor criar seus microserviços usando TDD ou não, mas independente da escolha, é necessário que todo esteja propriamente testado (unitário e integração).

## Principais desafios e próximos passos

### Desafios

Estudando e desenvolvendo essa arquitetura ficou claro que existe alguns pontos de atenção e acredito que o principal onde podemos errar é no tamanho e escopo de nossos microserviços. Quando falamos de microserviços é necessário esforçar-se para que cada um seja completamente desacoplado do outro, ou seja, tentar evitar ao máximo que haja a necessidade de comunicação entre eles e se isso não for possível, implementar filas para tentar desacoplá-los.

Essa segmentação de nossos microserviços por domínio será uma atividade de constante aprendizado e que provavelmente teremos bastante retrabalho no início.

### Próximos passos

- Desenhar e validar arquitetura cloud na AWS
- Automatizar build e deploy
- Montar infraestrutura via código 


