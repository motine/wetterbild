# -*- mode: ruby -*-

# Sometimes the boot stalls. This is due to cloud-init forcing the network to come up. This has a time out of ~ 2-3 minutes. Just wait...

NAME = File.basename(File.dirname(__FILE__)).gsub(/[^\w]/, '')

# MIRRORS = "sed -i 's@http://archive.ubuntu.com/ubuntu/@mirror://mirrors.ubuntu.com/mirrors.txt@g' /etc/apt/sources.list"

GENERAL = <<SCRIPT
apt-get -y install vim man wget
SCRIPT

# STUDIO = <<SCRIPT
# apt-get -y install unzip lib32stdc++6
# cd /opt
# wget –quiet https://dl.google.com/dl/android/studio/ide-zips/1.5.1.0/android-studio-ide-141.2456560-linux.zip
# unzip android-studio-ide-141.2456560-linux.zip
# rm android-studio-ide-141.2456560-linux.zip
# SCRIPT

DESKTOP = <<SCRIPT
apt-get -y install xfce4 virtualbox-guest-dkms virtualbox-guest-utils virtualbox-guest-x11
# apt-get -y install ubuntu-gnome-desktop gnome-shell
echo START X WITH "startx"
SCRIPT

Vagrant.configure("2") do |config|
  config.vm.box = "ubuntu/vivid64"
  config.vm.hostname = NAME

  config.vm.provision :shell, inline: GENERAL, keep_color: true
  config.vm.provision :shell, inline: DESKTOP, keep_color: true
  # config.vm.provision :shell, inline: STUDIO, keep_color: true

  config.vm.provider "virtualbox" do |vb|
    vb.name = NAME
    vb.memory = 4096
    vb.cpus = 2
    vb.gui = true
  end
end