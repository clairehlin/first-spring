curl --verbose -X POST 'http://localhost:8080/restaurants' -H 'Content-Type: application/json' \
    --data-raw '[
                 {"name":"Vaca Cafe",
                  "menus":
                          [
                            {
                            "name":"Christmas Menu",
                            "sections":[]
                            }
                          ]
                  }
    ]'
