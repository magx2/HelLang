| Token                | Changes to                                 |
| -------------------- | ------------------------------------------ |
| SIMPLE_TOKEN         | [a-zA-Z0-9-_]                              |
|                      | SIMPLE_TOKENSIMPLE_TOKEN                   |
| DEFINITION_TOKEN     | [a-zA-Z0-9-_]                              |
|                      | DEFINITION_TOKENDEFINITION_TOKEN           |
| SIMPLE_LABEL         | [a-z]                                      |
|                      | [a-z]SIMPLE_TOKEN                          |
| MODULE               | module MODULE_NAME                         |
| MODULE_NAME          | /SIMPLE_LABEL                              |
|                      | /MODULE_NAME                               |
| IMPORT               | import /MODULE_NAME/DEFINITION_TOKEN       |
| STRUCT_DEFINITION    | export struct STRUCT_NAME STRUCT_BODY      |
|                      | struct STRUCT_NAME STRUCT_BODY             |
| STRUCT_BODY          | { PROPERTY_DECLARATION }                   |
| PROPERTY_DECLARATION | SIMPLE_NAME: STRUCT_NAME                   |
| ^TODO                | SIMPLE_NAME: STRUCT_NAME [ PRECONDITIONS ] |
| FILE                 | MODULE STRUCT_DEFINITION                   |
|                      | MODULE STRUCT_DEFINITION STRUCT_DEFINITION |
