/*
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Library General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this program (see the file COPYING.LIB for more
 * details); if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package org.dcache.chimera.nfs.v4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dcache.chimera.nfs.ChimeraNFSException;
import org.dcache.chimera.nfs.nfsstat;
import org.dcache.chimera.nfs.v4.xdr.LAYOUTGET4res;
import org.dcache.chimera.nfs.v4.xdr.LAYOUTGET4resok;
import org.dcache.chimera.nfs.v4.xdr.layoutiomode4;
import org.dcache.chimera.nfs.v4.xdr.layouttype4;
import org.dcache.chimera.nfs.v4.xdr.length4;
import org.dcache.chimera.nfs.v4.xdr.nfs_argop4;
import org.dcache.chimera.nfs.v4.xdr.nfs_opnum4;
import org.dcache.chimera.nfs.v4.xdr.nfs_resop4;
import org.dcache.chimera.nfs.v4.xdr.uint64_t;

public class OperationLAYOUTGET extends AbstractNFSv4Operation {

    private static final Logger _log = LoggerFactory.getLogger(OperationLAYOUTGET.class);

    OperationLAYOUTGET(nfs_argop4 args) {
    super(args, nfs_opnum4.OP_LAYOUTGET);
    }

    @Override
    public nfs_resop4 process(CompoundContext context) {

    LAYOUTGET4res res = new LAYOUTGET4res();

    try {

        /*
         * dCache supports FILE layout
         *
         * LAYOUTGET is used by a client to get a layout for a file
         */

        /*
         * DRAFT-17: To get a layout from a specific offset through the
         * end-of-file, regardless of the file's length, a loga_length field
         * with all bits set to 1 (one) should be used
         */
        length4 lengthRange = new length4();
        lengthRange.value = new uint64_t(0xffffffff);

        if (_args.oplayoutget.loga_offset.value.value != 0) {
            if (_args.oplayoutget.loga_length.value.value == 0) {
                throw new ChimeraNFSException(nfsstat.NFSERR_INVAL, "length == 0");
            }

            /*
             * FIXME: sing/unsign issue here
             */
//            if ((_args.oplayoutget.loga_length.value.value + _args.oplayoutget.loga_offset.value.value) > lengthRange.value.value) {
//                throw new ChimeraNFSException(nfsstat.NFSERR_INVAL, "offset+length too big");
//            }
        }

        if ( !(_args.oplayoutget.loga_iomode  == layoutiomode4.LAYOUTIOMODE4_RW ||
				_args.oplayoutget.loga_iomode  == layoutiomode4.LAYOUTIOMODE4_READ)) {
            throw new ChimeraNFSException(nfsstat.NFSERR_BADIOMODE, "invalid loga_iomode");
        }

        if (_args.oplayoutget.loga_layout_type > 3) {
            throw new ChimeraNFSException(nfsstat.NFSERR_BADLAYOUT, "layouts supported but no matching found ("+ _args.oplayoutget.loga_layout_type +")");
        } else if (_args.oplayoutget.loga_layout_type != layouttype4.LAYOUT4_NFSV4_1_FILES) {
            throw new ChimeraNFSException(nfsstat.NFSERR_LAYOUTUNAVAILABLE, "layout not supported");
        }

        /*
         * FIXME: sing/unsign issue here
         */
//        if (_args.oplayoutget.loga_minlength.value.value < 1) {
//               throw new ChimeraNFSException(nfsstat.NFSERR_BADLAYOUT, "loga_minlength field should be at least one.");
//        }

        res.logr_resok4 = new LAYOUTGET4resok();

        Layout ioLayout = context.getDeviceManager().layoutGet(context.currentInode(),
                _args.oplayoutget.loga_iomode,
                context.getSession().getClient(),
                _args.oplayoutget.loga_stateid);

        res.logr_resok4.logr_layout = ioLayout.getLayoutSegments();
        res.logr_resok4.logr_stateid = ioLayout.getStateid();
        res.logr_resok4.logr_return_on_close = ioLayout.returnOnClose();

        res.logr_status = nfsstat.NFS_OK;

    } catch (ChimeraNFSException he) {
        _log.trace("LAYOUTGET: {}",  he.getMessage());
        res.logr_status = he.getStatus();
    } catch (Exception e) {
        _log.error("LAYOUTGET:", e);
        res.logr_status = nfsstat.NFSERR_SERVERFAULT;
    }

    _result.oplayoutget = res;
        return _result;

    }

}
