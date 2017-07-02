# Operation Types

Operation types supported by OrientDB

## Server (CONNECT Operations)

Command|Value as byte|Description|Async|Since
-------|-------------|-----------|-----|-----
REQUEST_SHUTDOWN|1|Shut down server.|no|
REQUEST_CONNECT|2|Required initial operation to access to server commands.|no|
REQUEST_DB_OPEN|3|Required initial operation to access to the database.|no|
REQUEST_DB_CREATE|4|Add a new database.|no|
REQUEST_DB_EXIST|6|Check if database exists.|no|
REQUEST_DB_DROP|7|Delete database.|no|
REQUEST_CONFIG_GET|70|Get a configuration property.|no|
REQUEST_CONFIG_SET|71|Set a configuration property.|no|
REQUEST_CONFIG_LIST|72|Get a list of configuration properties.|no|
REQUEST_DB_LIST|74|Get a list of databases.|no|1.0rc6

## Database (DB_OPEN Operations)

Command|Value as byte|Description|Async|Since
-------|-------------|-----------|-----|-----
REQUEST_DB_CLOSE|5|Close a database.|no|
REQUEST_DB_SIZE|8|Get the size of a database (in bytes).|no|0.9.25
REQUEST_DB_COUNTRECORDS|9|Get total number of records in a database.|no|0.9.25
REQUEST_DATACLUSTER_ADD (deprecated)|10|Add a data cluster.|no|
REQUEST_DATACLUSTER_DROP (deprecated)|11|Delete a data cluster.|no|
REQUEST_DATACLUSTER_COUNT (deprecated)|12|Get the total number of data clusters.|no|
REQUEST_DATACLUSTER_DATARANGE (deprecated)|13|Get the data range of data clusters.|no|
REQUEST_DATACLUSTER_COPY|14|Copy a data cluster.|no|
REQUEST_DATACLUSTER_LH_CLUSTER_IS_USED|16||no|1.2.0
REQUEST_RECORD_METADATA|29|Get metadata from a record.|no|1.4.0
REQUEST_RECORD_LOAD|30|Load a record.|no|
REQUEST_RECORD_LOAD_IF_VERSION_NOT_LATEST|44|Load a record.|no|2.1-rc4
REQUEST_RECORD_CREATE|31|Add a record.|yes|
REQUEST_RECORD_UPDATE|32||yes|
REQUEST_RECORD_DELETE|33|Delete a record.|yes|
REQUEST_RECORD_COPY|34|Copy a record.|yes|
REQUEST_RECORD_CLEAN_OUT|38|Clean out record.|yes|1.3.0
REQUEST_POSITIONS_FLOOR|39|Get the last record.|yes|1.3.0
REQUEST_COUNT (DEPRECATED)|40|See REQUEST_DATACLUSTER_COUNT|no|
REQUEST_COMMAND|41|Execute a command.|no|
REQUEST_POSITIONS_CEILING|42|Get the first record.|no|1.3.0
REQUEST_TX_COMMIT|60|Commit transaction.|no|
REQUEST_DB_RELOAD|73|Reload database.|no|1.0rc4
REQUEST_PUSH_RECORD|79||no|1.0rc6
REQUEST_PUSH_DISTRIB_CONFIG|80||no|1.0rc6
REQUEST_PUSH_LIVE_QUERY|81||no|2.1-rc2
REQUEST_DB_COPY|90||no|1.0rc8
REQUEST_REPLICATION|91||no|1.0
REQUEST_CLUSTER|92||no|1.0
REQUEST_DB_TRANSFER|93||no|1.0.2
REQUEST_DB_FREEZE|94||no|1.1.0
REQUEST_DB_RELEASE|95||no|1.1.0
REQUEST_DATACLUSTER_FREEZE (deprecated)|96||no|
REQUEST_DATACLUSTER_RELEASE (deprecated)|97||no|
REQUEST_CREATE_SBTREE_BONSAI|110|Creates an sb-tree bonsai on the remote server|no|1.7rc1
REQUEST_SBTREE_BONSAI_GET|111|Get value by key from sb-tree bonsai|no|1.7rc1
REQUEST_SBTREE_BONSAI_FIRST_KEY|112|Get first key from sb-tree bonsai|no|1.7rc1
REQUEST_SBTREE_BONSAI_GET_ENTRIES_MAJOR|113|Gets the portion of entries greater than the specified one. If returns 0 entries than the specified entrie is the largest|no|1.7rc1
REQUEST_RIDBAG_GET_SIZE|114|Rid-bag specific operation. Send but does not save changes of rid bag. Retrieves computed size of rid bag.|no|1.7rc1
REQUEST_INDEX_GET|120|Lookup in an index by key|no|2.1rc4
REQUEST_INDEX_PUT|121|Create or update an entry in an index|no|2.1rc4
REQUEST_INDEX_REMOVE|122|Remove an entry in an index by key|no|2.1rc4
REQUEST_INCREMENTAL_RESTORE|Incremental restore|no|2.2-rc1