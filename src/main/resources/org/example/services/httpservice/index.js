(function () {

    var Dial = function (container) {
        this.container = container;
        this.size = this.container.dataset.size;
        this.strokeWidth = this.size / 16;
        this.radius = (this.size / 2) - (this.strokeWidth / 2);
        this.value = this.container.dataset.value;
        this.direction = this.container.dataset.arrow;
        this.svg;
        this.defs;
        this.slice;
        this.sliceBefore;
        this.overlay;
        this.text;
        this.title = this.container.dataset.title;
        this.arrow;
        this.create();
    };

    Dial.prototype.create = function () {
        this.createSvg();
        this.createDefs();
        this.createSliceAfter();
        this.createSliceBefore();
        this.createText();
        this.createTitle();
        this.createArrow();
        this.container.appendChild(this.svg);
    };

    Dial.prototype.createSvg = function () {
        var svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
        svg.setAttribute('preserveAspectRatio', 'xMidYMid meet');
        svg.setAttribute('viewBox', '0 0 100 120');
        svg.setAttribute('width', '100%');
        svg.setAttribute('height', '100%');
        this.svg = svg;
    };

    Dial.prototype.createDefs = function () {
        var defs = document.createElementNS("http://www.w3.org/2000/svg", "defs");
        var linearGradient = document.createElementNS("http://www.w3.org/2000/svg", "linearGradient");
        linearGradient.setAttribute('id', 'gradient');
        var stop1 = document.createElementNS("http://www.w3.org/2000/svg", "stop");
        stop1.setAttribute('stop-color', '#6E4AE2');
        stop1.setAttribute('offset', '0%');
        linearGradient.appendChild(stop1);
        var stop2 = document.createElementNS("http://www.w3.org/2000/svg", "stop");
        stop2.setAttribute('stop-color', '#78F8EC');
        stop2.setAttribute('offset', '100%');
        linearGradient.appendChild(stop2);
        var linearGradientBackground = document.createElementNS("http://www.w3.org/2000/svg", "linearGradient");
        linearGradientBackground.setAttribute('id', 'gradient-background');
        var stop1 = document.createElementNS("http://www.w3.org/2000/svg", "stop");
        stop1.setAttribute('stop-color', 'rgba(0, 0, 0, 0.2)');
        stop1.setAttribute('offset', '0%');
        linearGradientBackground.appendChild(stop1);
        var stop2 = document.createElementNS("http://www.w3.org/2000/svg", "stop");
        stop2.setAttribute('stop-color', 'rgba(0, 0, 0, 0.05)');
        stop2.setAttribute('offset', '100%');
        linearGradientBackground.appendChild(stop2);
        defs.appendChild(linearGradient);
        defs.appendChild(linearGradientBackground);
        this.svg.appendChild(defs);
        this.defs = defs;
    };

    Dial.prototype.createSliceAfter = function () {
        var slice = document.createElementNS("http://www.w3.org/2000/svg", "path");
        slice.setAttribute('fill', 'none');
        slice.setAttribute('stroke', 'url(#gradient)');
        slice.setAttribute('stroke-width', this.strokeWidth);
        slice.setAttribute('transform', 'translate(' + this.strokeWidth / 2 + ',' + this.strokeWidth / 2 + ')');
        slice.setAttribute('class', 'animate-draw');
        this.svg.appendChild(slice);
        this.slice = slice;
    };

    Dial.prototype.createSliceBefore = function () {
        var slice = document.createElementNS("http://www.w3.org/2000/svg", "path");
        slice.setAttribute('fill', 'none');
        slice.setAttribute('stroke', 'url(#gradient-background)');
        slice.setAttribute('stroke-width', this.strokeWidth);
        slice.setAttribute('transform', 'translate(' + this.strokeWidth / 2 + ',' + this.strokeWidth / 2 + ')');
        slice.setAttribute('class', 'animate-draw');
        this.svg.appendChild(slice);
        this.sliceBefore = slice;
    };

    Dial.prototype.createOverlay = function () {
        var r = this.size - (this.size / 2) - this.strokeWidth / 2;
        var circle = document.createElementNS("http://www.w3.org/2000/svg", "circle");
        circle.setAttribute('cx', this.size / 2);
        circle.setAttribute('cy', this.size / 2);
        circle.setAttribute('r', r);
        circle.setAttribute('fill', 'url(#gradient-background)');
        this.svg.appendChild(circle);
        this.overlay = circle;
    };

    Dial.prototype.createText = function () {
        var fontSize = this.size / 3.5;
        var text = document.createElementNS("http://www.w3.org/2000/svg", "text");
        text.setAttribute('x', (this.size / 2) + fontSize / 7.5);
        text.setAttribute('y', (this.size / 2) + fontSize / 4);
        text.setAttribute('font-family', 'Century Gothic, Lato');
        text.setAttribute('font-size', fontSize);
        text.setAttribute('fill', '#78F8EC');
        text.setAttribute('text-anchor', 'middle');
        var tspanSize = fontSize / 3;
        text.innerHTML = 0 + '<tspan font-size="' + tspanSize + '" dy="' + -tspanSize * 1.2 + '">%</tspan>';
        this.svg.appendChild(text);
        this.text = text;
    };

    Dial.prototype.createTitle = function () {
        var fontSize = 12;
        var title = document.createElementNS("http://www.w3.org/2000/svg", "text");
        title.setAttribute('x', (this.size / 2));
        title.setAttribute('y', (this.size / 2) + 70);
        title.setAttribute('font-family', 'Century Gothic, Lato');
        title.setAttribute('font-size', fontSize);
        title.setAttribute('fill', '#78F8EC');
        title.setAttribute('text-anchor', 'middle');
        var tspanSize = fontSize / 3;
        title.innerHTML = this.title;
        this.svg.appendChild(title);
        this.title = title;
    };

    Dial.prototype.createArrow = function () {
        var arrowSize = this.size / 10;
        var arrowYOffset, m;
        if (this.direction === 'up') {
            arrowYOffset = arrowSize / 2;
            m = -1;
        } else if (this.direction === 'down') {
            arrowYOffset = 0;
            m = 1;
        }
        var arrowPosX = ((this.size / 2) - arrowSize / 2);
        var arrowPosY = (this.size - this.size / 3) + arrowYOffset;
        var arrowDOffset = m * (arrowSize / 1.5);
        var arrow = document.createElementNS("http://www.w3.org/2000/svg", "path");
        arrow.setAttribute('d', 'M 0 0 ' + arrowSize + ' 0 ' + arrowSize / 2 + ' ' + arrowDOffset + ' 0 0 Z');
        arrow.setAttribute('fill', '#97F8F0');
        arrow.setAttribute('opacity', '0.6');
        arrow.setAttribute('transform', 'translate(' + arrowPosX + ',' + arrowPosY + ')');
        this.svg.appendChild(arrow);
        this.arrow = arrow;
    };

    Dial.prototype.animateStart = function () {
        this.setValueAfter(33);
        this.setValueBefore(66);
    };

    Dial.prototype.animateReset = function () {
        this.setValueAfter(0);
        this.setValueBefore(0);
    };

    Dial.prototype.polarToCartesian = function (centerX, centerY, radius, angleInDegrees) {
        var angleInRadians = (angleInDegrees - 90) * Math.PI / 180.0;
        return {
            x: centerX + (radius * Math.cos(angleInRadians)),
            y: centerY + (radius * Math.sin(angleInRadians))
        };
    };

    Dial.prototype.describeArc = function (x, y, radius, startAngle, endAngle) {
        var start = this.polarToCartesian(x, y, radius, endAngle);
        var end = this.polarToCartesian(x, y, radius, startAngle);
        var largeArcFlag = endAngle - startAngle <= 180 ? "0" : "1";
        var d = [
            "M", start.x, start.y,
            "A", radius, radius, 0, largeArcFlag, 0, end.x, end.y
        ].join(" ");
        return d;
    };

    Dial.prototype.setValueAfter = function (value) {
        var c = (value / 100) * 360;
        if (c === 360)
            c = 359.99;
        var xy = this.size / 2 - this.strokeWidth / 2;
        var d = this.describeArc(xy, xy, xy, 180, 180 + c);
        this.slice.setAttribute('d', d);
        var tspanSize = (this.size / 3.5) / 3;
        this.text.innerHTML = Math.floor(value) + '<tspan font-size="' + tspanSize + '" dy="' + -tspanSize * 1.2 + '">%</tspan>';
    };


    Dial.prototype.setValueBefore = function (value) {
        var c = (value / 100) * 360;
        if (c === 360)
            c = 359.99;
        var xy = this.size / 2 - this.strokeWidth / 2;
        var d = this.describeArc(xy, xy, xy, 180, 180 + c);
        this.sliceBefore.setAttribute('d', d);
    };


    var exampleSocket = new WebSocket("ws://" + window.document.location.hostname + ":9000");
    exampleSocket.onopen = function () {
        document.querySelectorAll("#connection")[0].innerText = "open connection";
        document.querySelectorAll("#messages")[0].innerText = "";
        document.querySelectorAll("#error")[0].innerText = "";
    };

    exampleSocket.onerror = function () {
        document.querySelectorAll("#messages")[0].innerText = "";
        document.querySelectorAll("#error")[0].innerText = "error";
    };

    exampleSocket.onmessage = function (event) {
        document.querySelectorAll("#messages")[0].innerText = "messages recieving";
        document.querySelectorAll("#error")[0].innerText = "";
        try {
            const json = JSON.parse(event.data);
            document.querySelectorAll("#ClutchBefore")[0].innerText = json.clutch.before;
            document.querySelectorAll("#ClutchAfter")[0].innerText = json.clutch.after;
            dial1.setValueAfter(json.clutch.after);

            document.querySelectorAll("#BrakeBefore")[0].innerText = json.brake.before;
            document.querySelectorAll("#BrakeAfter")[0].innerText = json.brake.after;
            dial2.setValueAfter(json.brake.after);

            document.querySelectorAll("#ThrottleBefore")[0].innerText = json.throttle.before;
            document.querySelectorAll("#ThrottleAfter")[0].innerText = json.throttle.after;
            dial3.setValueAfter(json.throttle.after);
        } catch (e) {
        }
    };

    exampleSocket.onclose = function () {
        document.querySelectorAll("#connection")[0].innerText = "closed connection";
        document.querySelectorAll("#messages")[0].innerText = "";
    };


    var containers1 = document.getElementsByClassName("chart1");
    var dial1 = new Dial(containers1[0]);
    dial1.animateStart();

    var containers2 = document.getElementsByClassName("chart2");
    var dial2 = new Dial(containers2[0]);
    dial2.animateStart();

    var containers3 = document.getElementsByClassName("chart3");
    var dial3 = new Dial(containers3[0]);
    dial3.animateStart();

})()

