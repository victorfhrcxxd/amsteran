/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.serverpackets.KeyPacket;

import hwid.Hwid;
import hwid.HwidConfig;

public final class ProtocolVersion extends L2GameClientPacket
{
	private int _version;
	private byte _data[];
	private String _hwidHdd = "NoHWID-HD";
	private String _hwidMac = "NoHWID-MAC";
	private String _hwidCPU = "NoHWID-CPU";
	private String _hwidPCName = "NoHWID-PCNAME";
	private String _hwidUserName = "NoHWID-USERNAME";
	L2PcInstance player;

	@Override
	protected void readImpl()
	{
		_version = readD(); 
		if (Hwid.isProtectionOn())
		{
			if (_buf.remaining() > 260) 
			{ 
				_data = new byte[260]; 
				readB(_data);
				if (Hwid.isProtectionOn())
				{
					_hwidHdd = readS();
					_hwidMac = readS();
					_hwidCPU = readS();
					_hwidPCName = readS();
					_hwidUserName = readS();
				}
			}
		}
		else if (Hwid.isProtectionOn())
		{
			getClient().close(new KeyPacket(getClient().enableCrypt()));
		}
	}

	@Override
	protected void runImpl()
	{
		if (_version == -2)
		{
			getClient().closeNow();
		}
		else if (_version < Config.MIN_PROTOCOL_REVISION || _version > Config.MAX_PROTOCOL_REVISION)
		{
			_log.warning("Client: " + getClient().toString() + " -> Protocol Revision: " + _version + " is invalid. Minimum and maximum allowed are: " + Config.MIN_PROTOCOL_REVISION + " and " + Config.MAX_PROTOCOL_REVISION + ". Closing connection.");
			getClient().closeNow();
		}
		
		getClient().setRevision(_version);
		if (Hwid.isProtectionOn())
		{
			if (_hwidHdd.equals("NoGuard") && _hwidMac.equals("NoGuard") && _hwidCPU.equals("NoGuard") && _hwidPCName.equals("NoGuard") && _hwidUserName.equals("NoGuard"))
			{
				_log.info("HWID Status: No Client side dlls");
				getClient().close(new KeyPacket(getClient().enableCrypt()));
			}

			switch (HwidConfig.GET_CLIENT_HWID)
			{
			    case 1:
				    getClient().setHWID(_hwidHdd);
				    break;
			    case 2:
				    getClient().setHWID(_hwidMac);
				    break;
			    case 3:
				    getClient().setHWID(_hwidCPU);
				    break;
			    case 4:
				    getClient().setHWID(_hwidPCName);
				    break;
			    case 5:
				    getClient().setHWID(_hwidUserName);
				    break;
			}
		}

		getClient().sendPacket(new KeyPacket(getClient().enableCrypt()));
		getClient().setProtocolOk(true);
	}
}