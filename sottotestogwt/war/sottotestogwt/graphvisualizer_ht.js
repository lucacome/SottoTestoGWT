var labelType, useGradients, nativeTextSupport, animate;

(function() {
	  var ua = navigator.userAgent,
	      iStuff = ua.match(/iPhone/i) || ua.match(/iPad/i),
	      typeOfCanvas = typeof HTMLCanvasElement,
	      nativeCanvasSupport = (typeOfCanvas == 'object' || typeOfCanvas == 'function'),
	      textSupport = nativeCanvasSupport 
	        && (typeof document.createElement('canvas').getContext('2d').fillText == 'function');
	  //I'm setting this based on the fact that ExCanvas provides text support for IE
	  //and that as of today iPhone/iPad current text support is lame
	  labelType = (!nativeCanvasSupport || (textSupport && !iStuff))? 'Native' : 'HTML';
	  nativeTextSupport = labelType == 'Native';
	  useGradients = nativeCanvasSupport;
	  animate = !(iStuff || !nativeCanvasSupport);
	})();
  

var Log = {
  elem: false,
  write: function(text){
    if (!this.elem) 
      this.elem = document.getElementById('log');
    this.elem.innerHTML = text;
    this.elem.style.left = (500 - this.elem.offsetWidth / 2) + 'px';
  }
};


function init_ht(jdata){
    //init data
    var json = jdata;
    
    //end
    var infovis = document.getElementById('infovis');
    var w = infovis.offsetWidth - 50, h = infovis.offsetHeight - 50;
    
    //init Hypertree
    var ht = new $jit.Hypertree({
      //id of the visualization container
      injectInto: 'infovis',
      //canvas width and height
      width: w,
      height: h,
      //Change node and edge styles such as
      //color, width and dimensions.
      Node: {
          dim: 9,
          color: "#f00"
      },
      Edge: {
          lineWidth: 2,
          color: "#088"
      },
      
      onBeforeCompute: function(node){
          Log.write("centering");
      },
      //Attach event handlers and add text to the
      //labels. This method is only triggered on label
      //creation
      onCreateLabel: function(domElement, node){
          domElement.innerHTML = node.name;
          $jit.util.addEvent(domElement, 'click', function () {
              ht.onClick(node.id, {
                  onComplete: function() {
                      ht.controller.onComplete();
                  }
              });
          });
      },
      //Change node styles when labels are placed
      //or moved.
      onPlaceLabel: function(domElement, node){
          var style = domElement.style;
          style.display = '';
          style.cursor = 'pointer';
          if (node._depth <= 1) {
              style.fontSize = "0.8em";
              style.color = "#ddd";

          } else if(node._depth == 2){
              style.fontSize = "0.7em";
              style.color = "#555";

          } else {
              style.display = 'none';
          }

          var left = parseInt(style.left);
          var w = domElement.offsetWidth;
          style.left = (left - w / 2) + 'px';
      },
      
      onComplete: function(){
          Log.write("done");
          
          //Build the right column relations list.
          //This is done by collecting the information (stored in the data property) 
          //for all the nodes adjacent to the centered node.
          var node = ht.graph.getClosestNodeToOrigin("current");
          
          /* aggiunto da lollo per avere link wikipedia */
          var nodeName = node.name;
          nodeName = nodeName.replace("[", "");
          nodeName = nodeName.replace("]", "");
          
          var nodeLink = nodeName;
          nodeLink = nodeLink.replace(" ", "_");
          nodeLink = "<a href=\"http://en.wikipedia.org/wiki/"+nodeLink+"\" target=\"_blank\" class=\"graphNodeTitle\">"+nodeName+"</a>"
          
          var html = "<h4>" + nodeLink + "</h4><b><span class=\"graphConnectionsText\">Connections:</span></b>";
          html += "<ul>";
          node.eachAdjacency(function(adj){
              var child = adj.nodeTo;
              if (child.data) {
                  var rel = (child.data.band == node.name) ? child.data.relation : node.data.relation;
                  
                  /* aggiunto da lollo per avere link wikipedia */
                  var linkName = child.name;
                  linkName = linkName.replace("[", "");
                  linkName = linkName.replace("]", "");
                  
                  var link = linkName;
                  link = link.replace(" ", "_");                  
                  link = "<a href=\"http://en.wikipedia.org/wiki/"+link+"\" target=\"_blank\" class=\"graphWikiLink\">"+linkName+"</a>"
                  
                  // sego relation che e' inutile
                  relationOK= " " + "<div class=\"relation\">(relation: " + rel + ")</div></li>";
                  relationEmpty="";
                  html += "<li>" + link + relationEmpty;
              }
          });
          html += "</ul>";
          $jit.id('inner-details').innerHTML = html;
      }
    });
    //load JSON data.
    ht.loadJSON(json);
    //compute positions and plot.
    ht.refresh();
    //end
    ht.controller.onComplete();
}
