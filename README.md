# weather-collector

Fetches a city's current temperature from OpenWeatherMap once an hour, appends it to `<city>.csv`, and serves the readings as JSON over HTTP.

The default city is **Berlin** (`berlin.csv`).

## Setup

You need Java, [Leiningen](https://leiningen.org) and an [OpenWeatherMap API key](https://home.openweathermap.org/api_keys).

```sh
cp .env.example .env   # then fill in OPENWEATHER_API_KEY
set -a; source .env; set +a
lein run
```

| Variable              | Default  |                         |
|-----------------------|----------|-------------------------|
| `OPENWEATHER_API_KEY` | none     | required, exits without it |
| `CITY`                | `Berlin` | also names the CSV file |
| `PORT`                | `3000`   |                         |
| `DATA_DIR`            | `.`      | where the CSV is written |

## Example requests

```sh
curl localhost:3000/temperatures
# [{"timestamp":"2026-10-19T11:18:22","temperature":12.55},{"timestamp":"2026-10-19T12:35:20","temperature":11.82}]
```

Before the first reading has been saved:

```sh
curl localhost:3000/temperatures
# []
```

Any other route returns `404`.

## Tests

```sh
lein test
```

## Layout

- `weather.clj`: calls the OpenWeatherMap API
- `storage.clj`: appends to and reads the CSV
- `http.clj`: Ring handler and Jetty server
- `job.clj`: the hourly loop and failure handling
- `main.clj`: reads config and starts everything

Each namespace ends with a `(comment ...)` block. These are the REPL experiments I used to build it, left in as a record of how each piece came together.

## Assumptions

- The first fetch happens at startup, then every hour after the previous one. It is not aligned to the top of the hour and drifts by however long each fetch takes.
- Timestamps are UTC, in the same format as the example in the assignment pdf (no zone suffix).
- The CSV is written to `DATA_DIR` (the working directory by default). The file name is the lower-cased city, so `CITY="New York"` writes `new york.csv`.
- A failed fetch logs the error (including the API's response body) to stdout, writes nothing, and the next cycle tries again.
- `/temperatures` reads the whole file on every request. This is fine at one row per hour.

## In production I would

- Use a logging library instead of `println`.
- Use a real scheduler so fetches line up with the hour and the app can shut down cleanly.
- Skip malformed rows instead of failing the whole request.
