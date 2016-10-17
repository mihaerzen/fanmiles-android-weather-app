# Fanmiles android weather app

Simple weather app.

## How to make it work?

You need to have an appId from [https://openweathermap.org/api](https://openweathermap.org/api).
When you acquire the appId, create a resource file in `app/src/main/res/values/keys.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="OPEN_WEATHER_APP_ID" translatable="false">your-app-id</string>
</resources>

```
