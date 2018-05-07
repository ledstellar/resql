# resql
JDBC wrapper

### Constructor matching
#### By names
Each constructor parameter must be tied to query result column with unique name substituion.
At least one conversion from result column type to appropriate constructor parameter type
must exists.

Note, that parameter names unavailable in compiled classes by default. 
To enable use -parameters switch in class compilation.
#### By types
Each constructor parameter must have convertor from related query result column  
