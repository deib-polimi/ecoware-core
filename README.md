# ECoWare

ECoWare is a middleware based on a MAPE control loop that automatically changes the resource allocation of a set of containerized applications running on a shared cluster. The allocations is powered by a control-theory based planner.

This project contains a simple monitoring and analysis system that measure the response time of each deployed application and the implementation of the control theoretical planner. Moreover using this project an user can simulate workloads for each application using JMeter.

## Tests

ECoWare provides three built-in tests:

1. AWSAutoscalingTest that measure the performances of AWS Autoscaling (single app)
2. ECoWareVMTest that measure the performances of ECoWare using only the granularity of VMs (single app)
3. ECoWareContainerTest that measure the performances of ECoWare by means of scaling containers core allocation (multi-app)

The output of a test is a set of csv with the following header:

```
timestamp(ms),elapsed_time(ms),url,rt,mem(byte),cpu_core,users
```

The frequency of the entries depends on the `SAMPLE_TIME` configuration parameter.


## Configurations

ECoWare reads the `ecoware.properties` file for configuring the system. The documentation of this file can be found within the file itself. For each application that needs to be deployed a properties file must be written (see `example_test.properties` for an example).
