# kub-consumer-service
SpringBoot Micoservice Consumed Another Service with OAuth Run in K8s

Step-1                                                                     
Run the CI/CD Pipleline on Jenkins - kub_consumer_sevice_pipeline.This'll run three jobs.                       
Job-1.Chekout From Github and create jar file.                                   
Job-2.Create Docker Image by using Dockerfile and jar. This job also tag the Docker Image.                    
Job-3.Push Docker Image to Docker Hub.

Step-2                                                                    
Start kubernetes cluster either in local (minikube) or in GKE/AWS etc.               
To start minikube , Command: minikube --memory 4000 --cpus 1 start

Step-3                                                                                 
Create configmap from service properties file                                      
Command:k create configmap cm-consumer-sevice --from-file=/path/to/application.properties
To Verify the configmap                            
Command:k get configmap cm-consumer-sevice  -o yaml                                                     
To Replace the configmap after any property change                                    
Command:k create configmap cm-brands-client  --from-file=/path/to/application.properties -o yaml --dry-run | kubectl replace -f -

Step-4                                                            
Start service deployment from kub-consumer-service-dp.yaml                                               
Command: k create -f /path/to/kub-consumer-service-dp.yaml

    apiVersion: apps/v1beta1
    kind: Deployment
    metadata:
      name: dp-consumer-service
    spec:
      replicas: 1
      selector:
       matchLabels:
        app: pod-consumer-service
        env: dev
      template:
        metadata:
          labels:
            app: pod-consumer-service
            env: dev
        spec:
          containers:
          - name: cont-consumer-service
            image: ddas55/kub-consumer-service
            imagePullPolicy: Always
            ports:
            - containerPort: 8090
              protocol: TCP
            resources:
              requests:
                cpu: 100m
                memory: 100Mi
            volumeMounts:
            - name: vm-consumer-service
              mountPath: "/config/" 
              readOnly: true
            livenessProbe:
              httpGet:
                path: /svcone/hello/healthz
                port: 8090
                httpHeaders:
                 - name: X-Custom-Header
                   value: Awesome
              initialDelaySeconds: 30
              periodSeconds: 15
            readinessProbe:
              httpGet:
                path: /svcone/hello/rediness
                port: 8090
                httpHeaders:
                 - name: X-Custom-Header
                   value: Awesome
              initialDelaySeconds: 5
          volumes:
          - name: vm-consumer-service
            configMap:
              name: cm-consumer-service
              items:
              - key: application.properties 
                path: application.properties

Step-5                                              
Start service and Ingress                                   
Check if ingress started , command: kubectl get ingresses

    apiVersion: v1
    kind: Service
    metadata:
      name: svc-consumer-service
      labels:
        app: pod-consumer-service
        env: dev
    spec:
      selector:
        app: pod-consumer-service
        env: dev
      type: NodePort
      ports:
      - name: http
        port: 8090
        targetPort: 8090
        nodePort: 31001
      - name: https
        port: 443
        targetPort: 8443
        nodePort: 31002
        ---
        apiVersion: extensions/v1beta1
        kind: Ingress
        metadata:
          name: ing-consumer-service
        spec:
          rules:
          - http: 
             paths:
              - path: /svcone
                backend:
                  serviceName: svc-consumer-service
                  servicePort: 8090
                  
Step-6 , Testing the service                                      
Verify if service started for local cluster (minikube) , command : minikube service svc-consumer-service              
Testing URL with NodePort                                                              
http://192.168.99.100:31001/svcone/hello/hw                                                           
Testing URL with Ingress                                                                          
https://192.168.99.100/svcone/hello/hw

Step-7 , horizontal Scale UP or Down                                                                     
Command: kubectl scale deployment dp-consumer-service --replicas=3           

Step-8 , Rolling Update                                       
Command: kubectl set image deployment dp-consumer-service cont-consumer-service=ddas55/kub-consumer-service:new version

