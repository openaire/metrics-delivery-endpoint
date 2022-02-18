# metrics-delivery-endpoint
Metrics Acquisition System RESTful endpoint

## Configuration

The metrics definitions come from two JSON files: `metadata_mappings.json` and `metrics_mappings.json`.
Their location is determined by the `config.directory` application property - `${user.home}/mas` by default.

### Metadata mappings

This file contains the metric descriptions returned by `/metrics/{resourceId}/{metricId}`.
It has the following structure:

```json
{
  "resourceId": {
    "metricId": {
	  "name": "...", "description": "...", "unit": "...", "type": "..."
	}
  }
}
```

where the metadata are a metric name, description, its unit and whether it is a "STATE" or "EVENT" metric.

### Metrics mappings

This file defines how to return the metric value from `/metrics/{resourceId}/{metricId}/value`.
It has the same general structure:

```json
{
  "resourceId": {
    "metricId": {
	  "query": "...", "isCounter": "false"
	}
  }
}
```

The `isCounter` field is unused at the moment.

The query is normally a PromQL expression that is used to calculate the metric value.
For more complex cases it can reference a custom computation implemented in Java using a SpEL expression.

#### Complex cases

##### Range vectors

In PromQL range vectors are specified by giving their duration. We sometimes need to calculate eg. `increase` over a range that starts a specific point in time instead. For this placing a special "#R#" token instead of the range duration is supported, eg. `{"query": "increase(some_series[#R#])"}`

Before the query is sent to Prometheus the token is replaced with the duration in seconds between the query time and the value of the `counter.range.start` application property.

##### Custom computations

Instead of a PromQL query the `query` can be "#" followed by a [Spring Expression Language](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#expressions) (SpEL) expression.

This is used for cases where a single PromQL query cannot be used to get the interesting value and more complex processing is needed.

To get the metric value the expression is evaluated against an instance of the `ExpressionContext` class. Methods in this class have access to the Prometheus API client interface (`PrometheusApiClient`) and are responsible for the actual complex processing needed.

For example there is a method `sumPeriods` in `ExpressionContext` and a query of `"#sumPeriods('series_one', 'series_two', 42)"`will cause it to be called with the provided parameters and calculate the metric value to return.
