# Healthcare Alerting System

## Part-I: Real-Time Alerting Engine

#### Requirements
- Java 11
- Maven
- Docker

#### Docker
- Install Docker and check its installation: `docker-compose --version`
- Navigate to the docker directory: `cd docker`
- Run `docker-compose up -d` to start the kafka service
- Run `docker ps` to check whether the containers are up. There will be 2 images- `confluentinc/cp-kafka:latest` and `confluentinc/cp-zookeeper:latest`

#### Flink Application
- Navigate to the Real-Time-Alerting-Engine directory: `cd Real-Time-Alerting-Engine/`
- Build the application: `mvn clean install`
- Run the application: `java -jar target/Real-Time-Alerting-Engine-1.0.jar`


## Part-II: Predictive Alerting System

#### Initial Setup
- Navigate to the project's root: `cd <repository-path>`
- Create a virtual environment: `python3 -m venv venv`
- Activate the environment: `source venv/bin/activate`
- Install all the dependencies if there are any: `pip3 install -r requirements.txt`
- Navigate to the Predictive-Alerting-Engine directory `cd Predictive-Alerting-Engine/`
- Make the changes.
- To deactivate the environment: `deactivate`

#### Note: Please run `pip3 freeze > requirements.txt`, if you even install a new python library.


## Git Steps

### To start working
- `git checkout main`
- `git pull`
- To create a new working branch: `git checkout -tb <new-working-branch-name>`

### To push your changes
- Make sure you are on the correct branch. To check: `git branch`
- `git add .`
- `git commit -m "<commit-message>"`
- `git push -u origin <branch-name>` or `git push -u origin main` (if on the master branch)


## Directory and File Structure

    .
    ├── dataset/                                     # Results of predictive models used as a dataset in Real-Time Alerting Engine
        ├── results/                                 
        └── combined_data_with_survival.csv          # The original dataset created
    ├── Dataset Generation/                          # Python scripts for creating and extrapolating the dataset
    ├── docker/                                      # Docker configuration
    ├── docs/                                        # Documentation
    ├── Predictive-Alerting-Engine/                  # Machine-Leaning Based System
        ├── notebooks/                               # Contains all the machine learning model's notebooks
        └── results/                                 # Comprehensive results obtained by machine learning models for all patients
    ├── Real-Time-Alerting-Engine/                   # Real-Time Alerting Application
        ├── src/main/java/alerting                   # Source code for Real-Time Alerting Engine
        └── pom.xml
    ├── .gitignore                                  
    ├── readme.md
    └── ...
