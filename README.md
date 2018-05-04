# DeepNetwork

## Synopsis
DeepNetwork is a torrent build. It aims to propose changing the method of peer selection for torrent to prioritize network ping.

## Installation
For convenience, installation folders are included in the exe directory.
To run: 
1. Navigate to the ```exe/deepnetwork-server/bin``` or ```exe/deepnetwork-client/bin``` directories
2. Execute the deepnetwork script via the command ```./deepnetwork```
  
  * To run the server (Note: clients default server is 'ada'. i.e. run server on ada.)
  1. Move any files to be torrented to ```deepnetwork-server/bin/to_torrent```
  2. In ```deepnetwork-server/bin```, run ```./deepnetwork```
  All files will be segmented and hashed.
  
  * To run a drone
  1. In ```deepnetwork-drone/bin```, run ```./deepnetwork```
  2. Request a list of available files and select the ones to download and seed.  
  (Note: You will need to request the file on all drones to log the drone as a peer.)  
  (Files will not have to downloaded repeatedly on the same Sun Lab account.)
  
  * To run the client
  1. In ```deepnetwork-client/bin```, run ```./deepnetwork```

## License
Jiva Priya LLC
