/*
*		Virtual momentary switch/button to execute a URI/URL
*
*/
// ********************************************************************************************************************
preferences {
	section("External Access"){
		input "external_pushed_uri", "text", title: "External Pushed URI (http://x.x.x.x:port/blah?q=this)", required: false
        input "external_held_uri", "text", title: "External Held URI (http://x.x.x.x:port/blah?q=this)", required: false
	}
    
	section("Internal Access"){
		input "internal_ip", "text", title: "Internal IP", required: false
		input "internal_port", "text", title: "Internal Port (if not 80)", required: false
		input "internal_pushed_path", "text", title: "Internal Pushed Path (/blah?q=this)", required: false
        input "internal_held_path", "text", title: "Internal Held Path (/blah?q=this)", required: false
	}
}
// ********************************************************************************************************************

metadata {
    definition (name: "URI Button", 
	    namespace: "JayUK", 
        author: "JayUK")
        
    {
        capability "Actuator"
        capability "Button"
        capability "Sensor"

        command "pushed1"
        command "held1"
	}

	simulator {}
	
    tiles {
		standardTile("button", "device.button", width: 1, height: 1) {
			state "default", label: "", icon: "st.unknown.zwave.remote-controller", backgroundColor: "#ffffff"
		}
 		standardTile("pressed1", "device.button", width: 1, height: 1, decoration: "flat") {
			state "default", label: "Pushed 1", backgroundColor: "#ffffff", action: "pushed1"
		} 
 		standardTile("held1", "device.button", width: 1, height: 1, decoration: "flat") {
			state "default", label: "Held 1", backgroundColor: "#ffffff", action: "held1"
		}          
		main "button"
		details(["button","pushed1","held1"])
	}
}
// ********************************************************************************************************************
def parse(String description) {
	log.debug "URI Button: $description"
}
// ********************************************************************************************************************
def held1() {
	if (external_held_uri){
		def cmd = "${settings.external_held_uri}";

		log.debug "URI Switch: Sending request cmd[${cmd}]"

        httpGet(cmd) {resp ->
            if (resp.data) {
                log.info "URI Button: ${resp.data}"
            } 
        }
        sendHubCommand(result)
        sendEvent(name: "button", value: "held", data: [buttonNumber: "1"], descriptionText: "$device.displayName button 1 was held", isStateChange: true) 
        log.debug "URI Button: Executing HELD" 
        log.debug "URI Button: $result"
 	}
	
    if (internal_held_path){
		def port
        
        if (internal_port){
            port = "${internal_port}"
        } else {
            port = 80
        }

		def result = new physicalgraph.device.HubAction(
            method: "GET",
            path: "${internal_held_path}",
            headers: [
	            HOST: "${internal_ip}:${port}"
            ]
		)
        sendHubCommand(result)
        sendEvent(name: "button", value: "held", data: [buttonNumber: "1"], descriptionText: "$device.displayName button 1 was held", isStateChange: true) 
        log.debug "URI Button: Executing HELD" 
        log.debug "URI Button: $result"
	}	
} 
// ********************************************************************************************************************
def pushed1() {
if (external_pushed_uri){
		def cmd = "${settings.external_pushed_uri}";

		log.debug "URI Switch: Sending request cmd[${cmd}]"

        httpGet(cmd) {resp ->
            if (resp.data) {
                log.info "URI Button: ${resp.data}"
            } 
        }
        sendHubCommand(result)
        sendEvent(name: "button", value: "pushed", data: [buttonNumber: "1"], descriptionText: "$device.displayName button 1 was pushed", isStateChange: true) 
        log.debug "URI Button: Executing PUSHED" 
        log.debug "URI Button: $result"
 	}
	
    if (internal_pushed_path){
		def port
        
        if (internal_port){
            port = "${internal_port}"
        } else {
            port = 80
        }

		def result = new physicalgraph.device.HubAction(
            method: "GET",
            path: "${internal_pushed_path}",
            headers: [
	            HOST: "${internal_ip}:${port}"
            ]
		)
        sendHubCommand(result)
        sendEvent(name: "button", value: "pushed", data: [buttonNumber: "1"], descriptionText: "$device.displayName button 1 was pushed", isStateChange: true) 
        log.debug "URI Button: Executing PUSHED" 
        log.debug "URI Button: $result"
	}	
}
// ********************************************************************************************************************
def installed() {
	log.trace "Executing 'installed'"
	initialize()
}
// ********************************************************************************************************************
def updated() {
	log.trace "Executing 'updated'"
	initialize()
}
// ********************************************************************************************************************
private initialize() {
	log.trace "Executing 'initialize'"

	sendEvent(name: "DeviceWatch-DeviceStatus", value: "online")
	sendEvent(name: "healthStatus", value: "online")
	sendEvent(name: "DeviceWatch-Enroll", value: [protocol: "cloud", scheme:"untracked"].encodeAsJson(), displayed: false)
}
