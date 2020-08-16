# zetema

[zetema](https://www.pagat.com/invented/zetema.html), a minimal DNS upstream proxy.

```
dig @127.0.0.1 -p 8080 apple.com; dig @127.0.0.1 -p 8080 amazon.com; dig @127.0.0.1 -p 8080 facebook.com
```

```
export ZETEMA_LISTEN_ADDRESS=0.0.0.0
export ZETEMA_LISTEN_PORT=8080
export ZETEMA_UPSTREAM_ADDRESS=9.9.9.9
export ZETEMA_UPSTREAM_PORT=53
export ZETEMA_LOG_QUERIES=true
export ZETEMA_PLUGIN_ANSWER_EXCLUDE="amazon.com;176.32.103.205,205.251.242.103"
```

TODO try to load config as YAML instead.
