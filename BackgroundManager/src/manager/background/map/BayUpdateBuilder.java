package manager.background.map;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import manager.background.objects.Bay;

/**
 *
 * @author Don
 */
public class BayUpdateBuilder {

    public String createMultyBaySingleUpdateStr(List<Bay> bays, int start, int end) {
        // todo write content
        return null;
    }

    public int populateMultibaySingleUpdateStr(long siteDbid, List<Bay> bays, int start, int end, int parameterIndex, PreparedStatement stmt) throws SQLException {
        // todo write content
        return parameterIndex;
    }

    public String createSingleBayUpdateStr(Bay bay) {

        return null;
    }

    public int populateSingleBayUpdateStr(Bay bay, int parameterIndex, PreparedStatement stmt) throws SQLException {

        return parameterIndex;
    }

}
