package infrastructure.utils;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.ext.h2.H2DataTypeFactory;

public class CustomH2DataTypeFactory extends H2DataTypeFactory {

    @Override
    public DataType createDataType(int sqlType, String sqlTypeName) throws DataTypeException {

        if (sqlTypeName.toUpperCase().contains("ENUM")) {
            return DataType.VARCHAR;
        }
        return super.createDataType(sqlType, sqlTypeName);
    }
}
