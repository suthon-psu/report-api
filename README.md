1. Built Docker image: 

    ```sh
    docker build -t ssuthon/bmk-report-api:latest .
    ```

2. Log in to Docker Hub:

    ```sh
    docker login
    ```    

3. Push the image to Docker Hub:

    ```sh 
    docker push ssuthon/bmk-report-api:latest
    ```