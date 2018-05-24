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







