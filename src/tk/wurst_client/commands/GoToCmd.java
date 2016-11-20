/*
 * Copyright � 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.commands;

import net.minecraft.util.math.BlockPos;
import tk.wurst_client.ai.PathFinder;
import tk.wurst_client.commands.Cmd.Info;
import tk.wurst_client.utils.EntityUtils.TargetSettings;

@Info(description = "Walks or flies you to a specific location.",
	name = "goto",
	syntax = {"<x> <y> <z>", "<entity>"},
	help = "Commands/goto")
public class GoToCmd extends Cmd
{
	private TargetSettings targetSettings = new TargetSettings()
	{
		@Override
		public boolean targetFriends()
		{
			return true;
		}
		
		@Override
		public boolean targetBehindWalls()
		{
			return true;
		};
	};
	
	@Override
	public void execute(String[] args) throws Error
	{
		if(wurst.mods.goToCmdMod.isEnabled())
		{
			wurst.mods.goToCmdMod.setEnabled(false);
			return;
		}
		
		int[] pos = argsToPos(targetSettings, args);
		if(Math.abs(pos[0] - mc.thePlayer.posX) > 256
			|| Math.abs(pos[2] - mc.thePlayer.posZ) > 256)
		{
			wurst.chat.error("Goal is out of range!");
			wurst.chat.message("Maximum range is 256 blocks.");
			return;
		}
		int[] goal = argsToPos(targetSettings, args);
		PathFinder pathFinder =
			new PathFinder(new BlockPos(goal[0], goal[1], goal[2]));
		wurst.mods.goToCmdMod.setPathFinder(pathFinder);
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				System.out.println("Finding path");
				long startTime = System.nanoTime();
				if(pathFinder.find())
					wurst.mods.goToCmdMod.setEnabled(true);
				else
					wurst.chat.error("Could not find a path.");
				System.out.println("Done after "
					+ (System.nanoTime() - startTime) / 1e6 + "ms");
			}
		});
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}
}
