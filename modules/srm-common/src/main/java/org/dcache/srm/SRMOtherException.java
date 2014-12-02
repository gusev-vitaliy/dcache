/* dCache - http://www.dcache.org/
 *
 * Copyright (C) 2014 Deutsches Elektronen-Synchrotron
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.dcache.srm;

import org.dcache.srm.v2_2.TStatusCode;

public class SRMOtherException extends SRMException
{
    private TStatusCode statusCode;

    public SRMOtherException(TStatusCode statusCode)
    {
        this.statusCode = statusCode;
    }

    public SRMOtherException(TStatusCode statusCode, String message)
    {
        super(message);
        this.statusCode = statusCode;
    }

    public SRMOtherException(TStatusCode statusCode, String message, Throwable cause)
    {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public SRMOtherException(TStatusCode statusCode, Throwable cause)
    {
        super(cause);
        this.statusCode = statusCode;
    }

    @Override
    public TStatusCode getStatusCode()
    {
        return statusCode;
    }
}