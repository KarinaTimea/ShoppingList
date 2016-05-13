  var ref = new Firebase('https://karinasshopping.firebaseio.com/');
  
  var productsRef = ref.child("products");
  var productsList = document.getElementById("productsList");
 
  
 document.getElementById("addButton").addEventListener("click", function(){
	 
	var productName = document.getElementById("productInput").value;
  	var productQuantity = parseInt(document.getElementById("productQuantity").value);
	 
	  productsRef.push({
		  name: productName, 
		  quantity: productQuantity
	  });
  });   
  
 productsRef.on("child_added", function(snapshot) {
	  var listItem = snapshot.val();
  
	  var listElement = document.createElement("li");
	  var checkboxElement = document.createElement("input");
	  checkboxElement.type = "checkbox";
	  if (listItem.quantity != 0) {
		  var productListElement = document.createTextNode(" " + listItem.quantity + " " + listItem.name);
	  }
	  
	  listElement.appendChild(checkboxElement);
	  listElement.appendChild(productListElement);
	  productsList.appendChild(listElement);
  
  
  document.getElementById("deleteButton").addEventListener("click", function() {
	  if (checkboxElement.checked) {
		  snapshot.ref().remove();
		  productsList.removeChild(listElement);
	  }
  });
});