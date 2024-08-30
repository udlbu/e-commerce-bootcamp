# Project directory layout
```
├── src
│   ├── it      (Integration tests)
│   ├── main    (Application code)
│   ├── test    (Unit tests)


```

```
├── src/main
│   ├── com
│   │   ├── ecommerce
│   │   │   ├── product      (Each domain is in a separate directory)
│   │   │   │   ├── adapter  (External clients like REST services, DB)
│   │   │   │   ├── api      (Exposed endpoints)
│   │   │   │   ├── config   (Infrastructure configuration)
│   │   │   │   ├── domain   (Business logic goes here)
│   │   │   ├── shared       (Common classes used across domains)
│   │   │   │   ├── adapter
│   │   │   │   ├── api      (e.g., maintenance endpoints)
│   │   │   │   ├── config
│   │   │   │   ├── domain

```
