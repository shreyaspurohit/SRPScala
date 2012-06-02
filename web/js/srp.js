Array.prototype.toHexString = function(){
	var list = this.concat();
    var result = [];
    for (var i = 0; i < list.length; i++) {
    	var hexVal = list[i].toString(16);
    	if(hexVal.length == 1){
    		hexVal = "0" + hexVal
    	}
        result.push(hexVal);
    }
    return(result.join(""));

};

String.prototype.getBytes = function () {
	  var bytes = [];
	  for (var i = 0; i < this.length; ++i) {
	    bytes.push(this.charCodeAt(i));
	  }
	  return bytes;
};


var BigInteger = __import( this,"titaniumcore.crypto.BigInteger" );
var SHA = __import( this,"titaniumcore.crypto.SHA" );
var SecureRandom = __import( this,"titaniumcore.crypto.SecureRandom" );
var algorithm = "SHA-512";
var noOfIter = 3;
var sha = SHA.create(algorithm);
var random = new SecureRandom();

var N = new BigInteger("d4c7f8a2b32c11b8fba9581ec4ba4f1b04215642ef7355e37c0fc0443ef756ea2c6b8eeb755a1c723027663caa265ef785b8ff6a9b35227a52d86633dbdfca43", 16);
var g = new BigInteger("2", 16);
var k = new BigInteger((H(N.toByteArray(), g.toByteArray())));

var I = "user";

function H(data, salt){	
	var result = data.concat(salt);
    result = sha.hash(result);
	for (var i = 1; i < noOfIter; i++) {
		result = sha.hash(result);
    }
	result= [0].concat(result)
    return result;
}

function u(A, B){
	return H(A,B);
}

function gPowXModN(x){
	return g.modPow(new BigInteger(x), N);
}

function gen32RandomBytes(){
	var temp = new Array(32);
	random.nextBytes(temp);
	temp[0] = 0;
	return temp;
}

function a(){
	return gen32RandomBytes();
}

function x(sValFromServer, password){
	return H(sValFromServer, password.getBytes());
}

function A(abytes){
	return gPowXModN(abytes).toByteArray();
}

function S(xv1, B, av1, uv1){
    var bx = g.modPow(xv1, N);
    var btmp = (B.add(N.multiply(k)).subtract(k.multiply(bx))).mod(N);
    var Sclient = (btmp.modPow(av1.add(uv1.multiply(xv1)), N)).mod(N);
    return Sclient.toByteArray().toHexString();    
 }

function currentSession(sValStr, BStr, aVal, Aval, password){
	if(sValStr.length == 0 || BStr.length == 0){
		alert("Received wrong data from server. Verify if talking to right server.")
		return "";
	}
	var sVal = new BigInteger(sValStr,16).toByteArray();
	var B = new BigInteger(BStr,16).toByteArray();
	sVal = updateServerSaltIfRequired(sVal);
	var uVal = u(Aval, B);
	var xVal = x(sVal,password);
	var Sval = S(new BigInteger((xVal)),new BigInteger((B)), new BigInteger((aVal)), new BigInteger((uVal)));
	return Sval;
}

function updateServerSaltIfRequired(ss){
	if(ss[0] > 0){
		return [0].concat(ss);
	}
	return ss;
}