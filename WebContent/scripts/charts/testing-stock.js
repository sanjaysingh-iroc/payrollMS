<html><head>
<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1"></head><body>// ==ClosureCompiler==
// @compilation_level SIMPLE_OPTIMIZATIONS

/**
 * @license Highcharts Stock v1.0 Beta (2011-07-05)
 *
 * (c) 2009-2011 Torstein Hønsi
 *
 * License: www.highcharts.com/license
 */

// JSLint options:
/*jslint forin: true */
/*global document, window, navigator, setInterval, clearInterval, clearTimeout, setTimeout, location, jQuery, $ */

(function() {
// encapsulated variables
var doc = document,
	win = window,
	math = Math,
	mathRound = math.round,
	mathFloor = math.floor,
	mathCeil = math.ceil,
	mathMax = math.max,
	mathMin = math.min,
	mathAbs = math.abs,
	mathCos = math.cos,
	mathSin = math.sin,
	mathPI = math.PI,
	deg2rad = mathPI * 2 / 360,


	// some variables
	userAgent = navigator.userAgent,
	isIE = /msie/i.test(userAgent) &amp;&amp; !win.opera,
	docMode8 = doc.documentMode === 8,
	isWebKit = /AppleWebKit/.test(userAgent),
	isFirefox = /Firefox/.test(userAgent),
	//hasSVG = win.SVGAngle || doc.implementation.hasFeature("http://www.w3.org/TR/SVG11/feature#BasicStructure", "1.1"),
	hasSVG = !!doc.createElementNS &amp;&amp; !!doc.createElementNS("http://www.w3.org/2000/svg", "svg").createSVGRect,
	SVG_NS = 'http://www.w3.org/2000/svg',
	Renderer,
	hasTouch = doc.documentElement.ontouchstart !== undefined,
	symbolSizes = {},
	idCounter = 0,
	timeFactor = 1, // 1 = JavaScript time, 1000 = Unix time
	garbageBin,
	defaultOptions,
	dateFormat, // function
	globalAnimation,
	pathAnim,


	// some constants for frequently used strings
	UNDEFINED,
	DIV = 'div',
	ABSOLUTE = 'absolute',
	RELATIVE = 'relative',
	HIDDEN = 'hidden',
	PREFIX = 'highcharts-',
	VISIBLE = 'visible',
	PX = 'px',
	NONE = 'none',
	M = 'M',
	L = 'L',
	/*
	 * Empirical lowest possible opacities for TRACKER_FILL
	 * IE6: 0.002
	 * IE7: 0.002
	 * IE8: 0.002
	 * IE9: 0.00000000001 (unlimited)
	 * FF: 0.00000000001 (unlimited)
	 * Chrome: 0.000001
	 * Safari: 0.000001
	 * Opera: 0.00000000001 (unlimited)
	 */
	TRACKER_FILL = 'rgba(192,192,192,'+ (hasSVG ? 0.000001 : 0.002) +')', // invisible but clickable
	//TRACKER_FILL = 'rgba(192,192,192,0.5)',
	NORMAL_STATE = '',
	HOVER_STATE = 'hover',
	SELECT_STATE = 'select',
	MILLISECOND = 'millisecond',
	SECOND = 'second',
	MINUTE = 'minute',
	HOUR = 'hour',
	DAY = 'day',
	WEEK = 'week',
	MONTH = 'month',
	YEAR = 'year',

	// constants for attributes
	FILL = 'fill',
	LINEAR_GRADIENT = 'linearGradient',
	STOPS = 'stops',
	STROKE = 'stroke',
	STROKE_WIDTH = 'stroke-width',

	// time methods, changed based on whether or not UTC is used
	makeTime,
	getMinutes,
	getHours,
	getDay,
	getDate,
	getMonth,
	getFullYear,
	setMinutes,
	setHours,
	setDate,
	setMonth,
	setFullYear,

	// check for a custom HighchartsAdapter defined prior to this file
	globalAdapter = win.HighchartsAdapter,
	adapter = globalAdapter || {},

	// Utility functions. If the HighchartsAdapter is not defined, adapter is an empty object
	// and all the utility functions will be null. In that case they are populated by the
	// default adapters below.
	each = adapter.each,
	grep = adapter.grep,
	map = adapter.map,
	merge = adapter.merge,
	addEvent = adapter.addEvent,
	removeEvent = adapter.removeEvent,
	fireEvent = adapter.fireEvent,
	animate = adapter.animate,
	stop = adapter.stop,

	// lookup over the types and the associated classes
	seriesTypes = {},
	hoverChart;
	
// The Highcharts namespace
win.Highcharts = {};

/**
 * Extend an object with the members of another
 * @param {Object} a The object to be extended
 * @param {Object} b The object to add to the first one
 */
function extend(a, b) {
	var n;
	if (!a) {
		a = {};
	}
	for (n in b) {
		a[n] = b[n];
	}
	return a;
}

/**
 * Take an array and turn into a hash with even number arguments as keys and odd numbers as
 * values. Allows creating constants for commonly used style properties, attributes etc.
 * Avoid it in performance critical situations like looping
 */
function hash() {
	var i = 0,
		args = arguments,
		length = args.length,
		obj = {};
	for (; i &lt; length; i++) {
		obj[args[i++]] = args[i];
	}
	return obj;
}

/**
 * Shortcut for parseInt
 * @param {Object} s
 * @param {Number} mag Magnitude
 */
function pInt(s, mag) {
	return parseInt(s, mag || 10);
}

/**
 * Check for string
 * @param {Object} s
 */
function isString(s) {
	return typeof s === 'string';
}

/**
 * Check for object
 * @param {Object} obj
 */
function isObject(obj) {
	return typeof obj === 'object';
}

/**
 * Check for number
 * @param {Object} n
 */
function isNumber(n) {
	return typeof n === 'number';
}

function log2lin(num) {
	return math.log(num) / math.LN10;
}
function lin2log(num) {
	return math.pow(10, num);
}

/**
 * Remove last occurence of an item from an array
 * @param {Array} arr
 * @param {Mixed} item
 */
function erase(arr, item) {
	var i = arr.length;
	while (i--) {
		if (arr[i] === item) {
			arr.splice(i, 1);
			break;
		}
	}
	//return arr;
}

/**
 * Returns true if the object is not null or undefined. Like MooTools' $.defined.
 * @param {Object} obj
 */
function defined (obj) {
	return obj !== UNDEFINED &amp;&amp; obj !== null;
}

/**
 * Set or get an attribute or an object of attributes. Can't use jQuery attr because
 * it attempts to set expando properties on the SVG element, which is not allowed.
 *
 * @param {Object} elem The DOM element to receive the attribute(s)
 * @param {String|Object} prop The property or an abject of key-value pairs
 * @param {String} value The value if a single property is set
 */
function attr(elem, prop, value) {
	var key,
		setAttribute = 'setAttribute',
		ret;

	// if the prop is a string
	if (isString(prop)) {
		// set the value
		if (defined(value)) {

			elem[setAttribute](prop, value);

		// get the value
		} else if (elem &amp;&amp; elem.getAttribute) { // elem not defined when printing pie demo...
			ret = elem.getAttribute(prop);
		}

	// else if prop is defined, it is a hash of key/value pairs
	} else if (defined(prop) &amp;&amp; isObject(prop)) {
		for (key in prop) {
			elem[setAttribute](key, prop[key]);
		}
	}
	return ret;
}
/**
 * Check if an element is an array, and if not, make it into an array. Like
 * MooTools' $.splat.
 */
function splat(obj) {
	if (!obj || obj.constructor !== Array) {
		obj = [obj];
	}
	return obj;
}



/**
 * Return the first value that is defined. Like MooTools' $.pick.
 */
function pick() {
	var args = arguments,
		i,
		arg,
		length = args.length;
	for (i = 0; i &lt; length; i++) {
		arg = args[i];
		if (typeof arg !== 'undefined' &amp;&amp; arg !== null) {
			return arg;
		}
	}
}

/**
 * Set CSS on a given element
 * @param {Object} el
 * @param {Object} styles Style object with camel case property names
 */
function css (el, styles) {
	if (isIE) {
		if (styles &amp;&amp; styles.opacity !== UNDEFINED) {
			styles.filter = 'alpha(opacity='+ (styles.opacity * 100) +')';
		}
	}
	extend(el.style, styles);
}

/* *
 * Get CSS value on a given element
 * @param {Object} el DOM object
 * @param {String} styleProp Camel cased CSS propery
 * /
function getStyle (el, styleProp) {
	var ret,
		CURRENT_STYLE = 'currentStyle',
		GET_COMPUTED_STYLE = 'getComputedStyle';
	if (el[CURRENT_STYLE]) {
		ret = el[CURRENT_STYLE][styleProp];
	} else if (win[GET_COMPUTED_STYLE]) {
		ret = win[GET_COMPUTED_STYLE](el, null).getPropertyValue(hyphenate(styleProp));
	}
	return ret;
}*/

/**
 * Utility function to create element with attributes and styles
 * @param {Object} tag
 * @param {Object} attribs
 * @param {Object} styles
 * @param {Object} parent
 * @param {Object} nopad
 */
function createElement (tag, attribs, styles, parent, nopad) {
	var el = doc.createElement(tag);
	if (attribs) {
		extend(el, attribs);
	}
	if (nopad) {
		css(el, {padding: 0, border: NONE, margin: 0});
	}
	if (styles) {
		css(el, styles);
	}
	if (parent) {
		parent.appendChild(el);
	}
	return el;
}

/**
 * Extend a prototyped class by new members
 * @param {Object} parent
 * @param {Object} members
 */
function extendClass(parent, members) {
	var object = function(){};
	object.prototype = new parent();
	extend(object.prototype, members);
	return object;
}

/**
 * Format a number and return a string based on input settings
 * @param {Number} number The input number to format
 * @param {Number} decimals The amount of decimals
 * @param {String} decPoint The decimal point, defaults to the one given in the lang options
 * @param {String} thousandsSep The thousands separator, defaults to the one given in the lang options
 */
function numberFormat (number, decimals, decPoint, thousandsSep) {
	var lang = defaultOptions.lang,
		// http://kevin.vanzonneveld.net/techblog/article/javascript_equivalent_for_phps_number_format/
		n = number, c = isNaN(decimals = mathAbs(decimals)) ? 2 : decimals,
		d = decPoint === undefined ? lang.decimalPoint : decPoint,
		t = thousandsSep === undefined ? lang.thousandsSep : thousandsSep, s = n &lt; 0 ? "-" : "",
		i = String(pInt(n = mathAbs(+n || 0).toFixed(c))),
		j = i.length &gt; 3 ? i.length % 3 : 0;

	return s + (j ? i.substr(0, j) + t : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t) +
		(c ? d + mathAbs(n - i).toFixed(c).slice(2) : "");
}

/**
 * Based on http://www.php.net/manual/en/function.strftime.php
 * @param {String} format
 * @param {Number} timestamp
 * @param {Boolean} capitalize
 */
dateFormat = function (format, timestamp, capitalize) {
	function pad (number, length) {
		// two digits
		number = number.toString().replace(/^([0-9])$/, '0$1');
		// three digits
		if (length === 3) {
			number = number.toString().replace(/^([0-9]{2})$/, '0$1');
		}
		return number;
	}

	if (!defined(timestamp) || isNaN(timestamp)) {
		return 'Invalid date';
	}
	format = pick(format, '%Y-%m-%d %H:%M:%S');

	var date = new Date(timestamp * timeFactor),
		key, // used in for constuct below
		// get the basic time values
		hours = date[getHours](),
		day = date[getDay](),
		dayOfMonth = date[getDate](),
		month = date[getMonth](),
		fullYear = date[getFullYear](),
		lang = defaultOptions.lang,
		langWeekdays = lang.weekdays,
		langMonths = lang.months,
		/* // uncomment this and the 'W' format key below to enable week numbers
		weekNumber = function() {
			var clone = new Date(date.valueOf()),
				day = clone[getDay]() == 0 ? 7 : clone[getDay](),
				dayNumber;
			clone.setDate(clone[getDate]() + 4 - day);
			dayNumber = mathFloor((clone.getTime() - new Date(clone[getFullYear](), 0, 1, -6)) / 86400000);
			return 1 + mathFloor(dayNumber / 7);
		},
		*/

		// list all format keys
		replacements = {

			// Day
			'a': langWeekdays[day].substr(0, 3), // Short weekday, like 'Mon'
			'A': langWeekdays[day], // Long weekday, like 'Monday'
			'd': pad(dayOfMonth), // Two digit day of the month, 01 to 31
			'e': dayOfMonth, // Day of the month, 1 through 31

			// Week (none implemented)
			//'W': weekNumber(),

			// Month
			'b': langMonths[month].substr(0, 3), // Short month, like 'Jan'
			'B': langMonths[month], // Long month, like 'January'
			'm': pad(month + 1), // Two digit month number, 01 through 12

			// Year
			'y': fullYear.toString().substr(2, 2), // Two digits year, like 09 for 2009
			'Y': fullYear, // Four digits year, like 2009

			// Time
			'H': pad(hours), // Two digits hours in 24h format, 00 through 23
			'I': pad((hours % 12) || 12), // Two digits hours in 12h format, 00 through 11
			'l': (hours % 12) || 12, // Hours in 12h format, 1 through 12
			'M': pad(date[getMinutes]()), // Two digits minutes, 00 through 59
			'p': hours &lt; 12 ? 'AM' : 'PM', // Upper case AM or PM
			'P': hours &lt; 12 ? 'am' : 'pm', // Lower case AM or PM
			'S': pad(date.getSeconds()), // Two digits seconds, 00 through  59
			'L': pad(timestamp % 1000, 3) // Milliseconds (naming from Ruby)
		};


	// do the replaces
	for (key in replacements) {
		format = format.replace('%'+ key, replacements[key]);
	}

	// Optionally capitalize the string and return
	return capitalize ? format.substr(0, 1).toUpperCase() + format.substr(1) : format;
};

/**
 * Loop up the node tree and add offsetWidth and offsetHeight to get the
 * total page offset for a given element. Used by Opera and iOS on hover and
 * all browsers on point click.
 *
 * @param {Object} el
 *
 */
function getPosition (el) {
	var p = { left: el.offsetLeft, top: el.offsetTop };
	el = el.offsetParent;
	while (el) {
		p.left += el.offsetLeft;
		p.top += el.offsetTop;
		if (el !== doc.body &amp;&amp; el !== doc.documentElement) {
			p.left -= el.scrollLeft;
			p.top -= el.scrollTop;
		}
		el = el.offsetParent;
	}
	return p;
}

/**
 * Take an interval and normalize it to multiples of 1, 2, 2.5 and 5
 * @param {Number} interval
 * @param {Array} multiples
 * @param {Number} magnitude
 * @param {Object} options
 */
function normalizeTickInterval(interval, multiples, magnitude, options) {
	var normalized, i;

	// round to a tenfold of 1, 2, 2.5 or 5
	//magnitude = multiples ? 1 : math.pow(10, mathFloor(math.log(interval) / math.LN10));
	magnitude = pick(magnitude, 1);
	normalized = interval / magnitude;

	// multiples for a linear scale
	if (!multiples) {
		multiples = [1, 2, 2.5, 5, 10];
		//multiples = [1, 2, 2.5, 4, 5, 7.5, 10];

		// the allowDecimals option
		if (options &amp;&amp; options.allowDecimals === false) {
			if (magnitude === 1) {
				multiples = [1, 2, 5, 10];
			} else if (magnitude &lt;= 0.1) {
				multiples = [1 / magnitude];
			}
		}
	}

	// normalize the interval to the nearest multiple
	for (i = 0; i &lt; multiples.length; i++) {
		interval = multiples[i];
		if (normalized &lt;= (multiples[i] + (multiples[i+1] || multiples[i])) / 2) {
			break;
		}
	}

	// multiply back to the correct magnitude
	interval *= magnitude;

	return interval;
}

/**
 * Set the tick positions to a time unit that makes sense, for example
 * on the first of each month or on every Monday. Return an array
 * with the time positions. Used in datetime axes as well as for grouping
 * data on a datetime axis.
 *
 * @param {Number} tickInterval The approximate interval in axis values (ms)
 * @param {Number} min The minimum in axis values
 * @param {Number} max The maximum in axis values
 * @param {Number} startOfWeek
 * @param {Array} unitsOption
 */
function getTimeTicks(tickInterval, min, max, startOfWeek, unitsOption) {
	var tickPositions = [],
		i,
		useUTC = defaultOptions.global.useUTC,
		oneSecond = 1000 / timeFactor,
		oneMinute = 60000 / timeFactor,
		oneHour = 3600000 / timeFactor,
		oneDay = 24 * 3600000 / timeFactor,
		oneWeek = 7 * 24 * 3600000 / timeFactor,
		oneMonth = 30 * 24 * 3600000 / timeFactor,
		oneYear = 31556952000 / timeFactor,

		ranges = hash(
			MILLISECOND, 1,
			SECOND, oneSecond,
			MINUTE, oneMinute,
			HOUR, oneHour,
			DAY, oneDay,
			WEEK, oneWeek,
			MONTH, oneMonth,
			YEAR, oneYear
		),
		units = unitsOption || [[
			'millisecond',					// unit name
			//1,								// fixed incremental unit
			[1, 2, 5, 10, 20, 25, 50, 100, 200, 500]
		], [
			'second',						// unit name
			//oneSecond,						// fixed incremental unit
			[1, 2, 5, 10, 15, 30]			// allowed multiples
		], [
			'minute',						// unit name
			//oneMinute,						// fixed incremental unit
			[1, 2, 5, 10, 15, 30]			// allowed multiples
		], [
			'hour',							// unit name
			//oneHour,						// fixed incremental unit
			[1, 2, 3, 4, 6, 8, 12]			// allowed multiples
		], [
			'day',							// unit name
			//oneDay,							// fixed incremental unit
			[1, 2]							// allowed multiples
		], [
			'week',							// unit name
			//oneWeek,						// fixed incremental unit
			[1, 2]							// allowed multiples
		], [
			'month',
			//oneMonth,
			[1, 2, 3, 4, 6]
		], [
			'year',
			//oneYear,
			null
		]],

		unit = units[units.length - 1], // default unit is years
		interval = ranges[unit[0]],
		multiples = unit[1];

	// loop through the units to find the one that best fits the tickInterval
	for (i = 0; i &lt; units.length; i++)  {
		unit = units[i];
		interval = ranges[unit[0]];
		multiples = unit[1];


		if (units[i+1]) {
			// lessThan is in the middle between the highest multiple and the next unit.
			var lessThan = (interval * multiples[multiples.length - 1] +
						ranges[units[i + 1][0]]	) / 2;

			// break and keep the current unit
			if (tickInterval &lt;= lessThan) {
				break;
			}
		}
	}

	// prevent 2.5 years intervals, though 25, 250 etc. are allowed
	if (interval === oneYear &amp;&amp; tickInterval &lt; 5 * interval) {
		multiples = [1, 2, 5];
	}

	// get the minimum value by flooring the date
	var multitude = normalizeTickInterval(tickInterval / interval, multiples),
		minYear, // used in months and years as a basis for Date.UTC()
		minDate = new Date(min * timeFactor);

	minDate.setMilliseconds(0);

	if (interval &gt;= oneSecond) { // second
		minDate.setSeconds(interval &gt;= oneMinute ? 0 :
			multitude * mathFloor(minDate.getSeconds() / multitude));
	}

	if (interval &gt;= oneMinute) { // minute
		minDate[setMinutes](interval &gt;= oneHour ? 0 :
			multitude * mathFloor(minDate[getMinutes]() / multitude));
	}

	if (interval &gt;= oneHour) { // hour
		minDate[setHours](interval &gt;= oneDay ? 0 :
			multitude * mathFloor(minDate[getHours]() / multitude));
	}

	if (interval &gt;= oneDay) { // day
		minDate[setDate](interval &gt;= oneMonth ? 1 :
			multitude * mathFloor(minDate[getDate]() / multitude));
	}

	if (interval &gt;= oneMonth) { // month
		minDate[setMonth](interval &gt;= oneYear ? 0 :
			multitude * mathFloor(minDate[getMonth]() / multitude));
		minYear = minDate[getFullYear]();
	}

	if (interval &gt;= oneYear) { // year
		minYear -= minYear % multitude;
		minDate[setFullYear](minYear);
	}

	// week is a special case that runs outside the hierarchy
	if (interval === oneWeek) {
		// get start of current week, independent of multitude
		minDate[setDate](minDate[getDate]() - minDate[getDay]() +
			pick(startOfWeek, 1));
	}


	// get tick positions
	i = 1;
	minYear = minDate[getFullYear]();
	var time = minDate.getTime() / timeFactor,
		minMonth = minDate[getMonth](),
		minDateDate = minDate[getDate]();

	// iterate and add tick positions at appropriate values
	while (time &lt; max) {
		tickPositions.push(time);

		// if the interval is years, use Date.UTC to increase years
		if (interval === oneYear) {
			time = makeTime(minYear + i * multitude, 0) / timeFactor;

		// if the interval is months, use Date.UTC to increase months
		} else if (interval === oneMonth) {
			time = makeTime(minYear, minMonth + i * multitude) / timeFactor;

		// if we're using global time, the interval is not fixed as it jumps
		// one hour at the DST crossover
		} else if (!useUTC &amp;&amp; (interval === oneDay || interval === oneWeek)) {
			time = makeTime(minYear, minMonth, minDateDate +
				i * multitude * (interval === oneDay ? 1 : 7));

		// else, the interval is fixed and we use simple addition
		} else {
			time += interval * multitude;
		}

		i++;
	}
	// push the last time
	tickPositions.push(time);


	// record information on the chosen unit - for dynamic label formatter
	tickPositions.unit = unit;

	return tickPositions;
}

/**
 * Helper class that contains variuos counters that are local to the chart.
 */
function ChartCounters() {
	this.color = 0;
	this.symbol = 0;
}

ChartCounters.prototype =  {
	/**
	 * Wraps the color counter if it reaches the specified length.
	 */
	wrapColor: function(length) {
		if (this.color &gt;= length) {
			this.color = 0;
		}
	},

	/**
	 * Wraps the symbol counter if it reaches the specified length.
	 */
	wrapSymbol: function(length) {
		if (this.symbol &gt;= length) {
			this.symbol = 0;
		}
	}
};

/**
 * Set the global animation to either a given value, or fall back to the
 * given chart's animation option
 * @param {Object} animation
 * @param {Object} chart
 */
function setAnimation(animation, chart) {
	globalAnimation = pick(animation, chart.animation);
}

/*
 * Define the adapter for frameworks. If an external adapter is not defined,
 * Highcharts reverts to the built-in jQuery adapter.
 */
if (globalAdapter &amp;&amp; globalAdapter.init) {
	globalAdapter.init();
}
if (!globalAdapter &amp;&amp; win.jQuery) {
	var jQ = jQuery;

	/**
	 * Utility for iterating over an array. Parameters are reversed compared to jQuery.
	 * @param {Array} arr
	 * @param {Function} fn
	 */
	each = function(arr, fn) {
		var i = 0,
			len = arr.length;
		for (; i &lt; len; i++) {
			if (fn.call(arr[i], arr[i], i, arr) === false) {
				return i;
			}
		}
	};

	/**
	 * Filter an array
	 */
	grep = jQ.grep;

	/**
	 * Map an array
	 * @param {Array} arr
	 * @param {Function} fn
	 */
	map = function(arr, fn){
		//return jQuery.map(arr, fn);
		var results = [],
			i = 0, len = arr.length;
		for (; i &lt; len; i++) {
			results[i] = fn.call(arr[i], arr[i], i, arr);
		}
		return results;

	};

	/**
	 * Deep merge two objects and return a third object
	 */
	merge = function(){
		var args = arguments;
		return jQ.extend(true, null, args[0], args[1], args[2], args[3]);
	};

	/**
	 * Add an event listener
	 * @param {Object} el A HTML element or custom object
	 * @param {String} event The event type
	 * @param {Function} fn The event handler
	 */
	addEvent = function (el, event, fn){
		jQ(el).bind(event, fn);
	};

	/**
	 * Remove event added with addEvent
	 * @param {Object} el The object
	 * @param {String} eventType The event type. Leave blank to remove all events.
	 * @param {Function} handler The function to remove
	 */
	removeEvent = function(el, eventType, handler) {
		// workaround for jQuery issue with unbinding custom events:
		// http://forum.jquery.com/topic/javascript-error-when-unbinding-a-custom-event-using-jquery-1-4-2
		var func = doc.removeEventListener ? 'removeEventListener' : 'detachEvent';
		if (doc[func] &amp;&amp; !el[func]) {
			el[func] = function() {};
		}

		jQ(el).unbind(eventType, handler);
	};

	/**
	 * Fire an event on a custom object
	 * @param {Object} el
	 * @param {String} type
	 * @param {Object} eventArguments
	 * @param {Function} defaultFunction
	 */
	fireEvent = function(el, type, eventArguments, defaultFunction) {
		var event = jQ.Event(type),
			detachedType = 'detached'+ type;
		extend(event, eventArguments);

		// Prevent jQuery from triggering the object method that is named the
		// same as the event. For example, if the event is 'select', jQuery
		// attempts calling el.select and it goes into a loop.
		if (el[type]) {
			el[detachedType] = el[type];
			el[type] = null;
		}

		// trigger it
		jQ(el).trigger(event);

		// attach the method
		if (el[detachedType]) {
			el[type] = el[detachedType];
			el[detachedType] = null;
		}

		if (defaultFunction &amp;&amp; !event.isDefaultPrevented()) {
			defaultFunction(event);
		}
	};

	/**
	 * Animate a HTML element or SVG element wrapper
	 * @param {Object} el
	 * @param {Object} params
	 * @param {Object} options jQuery-like animation options: duration, easing, callback
	 */
	animate = function (el, params, options) {
		var $el = jQ(el);
		if (params.d) {
			el.toD = params.d; // keep the array form for paths, used in jQ.fx.step.d
			params.d = 1; // because in jQuery, animating to an array has a different meaning
		}

		$el.stop();
		$el.animate(params, options);

	};
	/**
	 * Stop running animation
	 */
	stop = function (el) {
		jQ(el).stop();
	};


	// extend jQuery
	jQ.extend( jQ.easing, {
		easeOutQuad: function (x, t, b, c, d) {
			return -c *(t/=d)*(t-2) + b;
		}
	});

	// extend the animate function to allow SVG animations
	var oldStepDefault = jQuery.fx.step._default,
		oldCur = jQuery.fx.prototype.cur;

	// do the step
	jQ.fx.step._default = function(fx){
		var elem = fx.elem;
		if (elem.attr) { // is SVG element wrapper
			elem.attr(fx.prop, fx.now);
		} else {
			oldStepDefault.apply(this, arguments);
		}
	};
	// animate paths
	jQ.fx.step.d = function(fx) {
		var elem = fx.elem;


		// Normally start and end should be set in state == 0, but sometimes,
		// for reasons unknown, this doesn't happen. Perhaps state == 0 is skipped
		// in these cases
		if (!fx.started) {
			var ends = pathAnim.init(elem, elem.d, elem.toD);
			fx.start = ends[0];
			fx.end = ends[1];
			fx.started = true;
		}


		// interpolate each value of the path
		elem.attr('d', pathAnim.step(fx.start, fx.end, fx.pos, elem.toD));

	};
	// get the current value
	jQ.fx.prototype.cur = function() {
		var elem = this.elem,
			r;
		if (elem.attr) { // is SVG element wrapper
			r = elem.attr(this.prop);
		} else {
			r = oldCur.apply(this, arguments);
		}
		return r;
	};
}

/**
 * Path interpolation algorithm used across adapters
 */
pathAnim = {
	/**
	 * Prepare start and end values so that the path can be animated one to one
	 */
	init: function(elem, fromD, toD) {
		fromD = fromD || '';
		var shift = elem.shift,
			bezier = fromD.indexOf('C') &gt; -1,
			numParams = bezier ? 7 : 3,
			endLength,
			slice,
			i,
			start = fromD.split(' '),
			end = [].concat(toD), // copy
			startBaseLine,
			endBaseLine,
			sixify = function(arr) { // in splines make move points have six parameters like bezier curves
				i = arr.length;
				while (i--) {
					if (arr[i] === M) {
						arr.splice(i + 1, 0, arr[i+1], arr[i+2], arr[i+1], arr[i+2]);
					}
				}
			};

		if (bezier) {
			sixify(start);
			sixify(end);
		}

		// pull out the base lines before padding
		if (elem.isArea) {
			startBaseLine = start.splice(start.length - 6, 6);
			endBaseLine = end.splice(end.length - 6, 6);
		}

		// if shifting points, prepend a dummy point to the end path
		if (shift === 1 ) {

			end = [].concat(end).splice(0, numParams).concat(end);
		}
		elem.shift = 0; // reset for following animations

		// copy and append last point until the length matches the end length
		if (start.length) {
			endLength = end.length;
			while (start.length &lt; endLength) {

				//bezier &amp;&amp; sixify(start);
				slice = [].concat(start).splice(start.length - numParams, numParams);
				if (bezier) { // disable first control point
					slice[numParams - 6] = slice[numParams - 2];
					slice[numParams - 5] = slice[numParams - 1];
				}
				start = start.concat(slice);
			}
		}

		if (startBaseLine) { // append the base lines for areas
			start = start.concat(startBaseLine);
			end = end.concat(endBaseLine);
		}
		return [start, end];
	},

	/**
	 * Interpolate each value of the path and return the array
	 */
	step: function(start, end, pos, complete) {
		var ret = [],
			i = start.length,
			startVal;

		if (pos === 1) { // land on the final path without adjustment points appended in the ends
			ret = complete;

		} else if (i === end.length &amp;&amp; pos &lt; 1) {
			while (i--) {
				startVal = parseFloat(start[i]);
				ret[i] =
					isNaN(startVal) ? // a letter instruction like M or L
						start[i] :
						pos * (parseFloat(end[i] - startVal)) + startVal;

			}
		} else { // if animation is finished or length not matching, land on right value
			ret = end;
		}
		return ret;
	}
};

/**
 * Set the time methods globally based on the useUTC option. Time method can be either
 * local time or UTC (default).
 */
function setTimeMethods() {
	var useUTC = defaultOptions.global.useUTC;

	makeTime = useUTC ? Date.UTC : function(year, month, date, hours, minutes, seconds) {
		return new Date(
			year,
			month,
			pick(date, 1),
			pick(hours, 0),
			pick(minutes, 0),
			pick(seconds, 0)
		).getTime();
	};
	getMinutes = useUTC ? 'getUTCMinutes' : 'getMinutes';
	getHours = useUTC ? 'getUTCHours' : 'getHours';
	getDay = useUTC ? 'getUTCDay' : 'getDay';
	getDate = useUTC ? 'getUTCDate' : 'getDate';
	getMonth = useUTC ? 'getUTCMonth' : 'getMonth';
	getFullYear = useUTC ? 'getUTCFullYear' : 'getFullYear';
	setMinutes = useUTC ? 'setUTCMinutes' : 'setMinutes';
	setHours = useUTC ? 'setUTCHours' : 'setHours';
	setDate = useUTC ? 'setUTCDate' : 'setDate';
	setMonth = useUTC ? 'setUTCMonth' : 'setMonth';
	setFullYear = useUTC ? 'setUTCFullYear' : 'setFullYear';

}

/**
 * Merge the default options with custom options and return the new options structure
 * @param {Object} options The new custom options
 */
function setOptions(options) {
	defaultOptions = merge(defaultOptions, options);

	// apply UTC
	setTimeMethods();

	return defaultOptions;
}

/**
 * Get the updated default options. Merely exposing defaultOptions for outside modules
 * isn't enough because the setOptions method creates a new object.
 */
function getOptions() {
	return defaultOptions;
}

/**
 * Discard an element by moving it to the bin and delete
 * @param {Object} The HTML node to discard
 */
function discardElement(element) {
	// create a garbage bin element, not part of the DOM
	if (!garbageBin) {
		garbageBin = createElement(DIV);
	}

	// move the node and empty bin
	if (element) {
		garbageBin.appendChild(element);
	}
	garbageBin.innerHTML = '';
}

/* ****************************************************************************
 * Handle the options                                                         *
 *****************************************************************************/
var

defaultLabelOptions = {
	enabled: true,
	// rotation: 0,
	align: 'center',
	x: 0,
	y: 15,
	/*formatter: function() {
		return this.value;
	},*/
	style: {
		color: '#666',
		fontSize: '11px',
		lineHeight: '14px'
	}
};

defaultOptions = {
	colors: ['#4572A7', '#AA4643', '#89A54E', '#80699B', '#3D96AE',
		'#DB843D', '#92A8CD', '#A47D7C', '#B5CA92'],
	symbols: ['circle', 'diamond', 'square', 'triangle', 'triangle-down'],
	lang: {
		loading: 'Loading...',
		months: ['January', 'February', 'March', 'April', 'May', 'June', 'July',
				'August', 'September', 'October', 'November', 'December'],
		weekdays: ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'],
		decimalPoint: '.',
		resetZoom: 'Reset zoom',
		resetZoomTitle: 'Reset zoom level 1:1',
		thousandsSep: ','
	},
	global: {
		useUTC: true
	},
	chart: {
		//animation: true,
		//alignTicks: false,
		//reflow: true,
		//className: null,
		//events: { load, selection },
		//margin: [null],
		//marginTop: null,
		//marginRight: null,
		//marginBottom: null,
		//marginLeft: null,
		borderColor: '#4572A7',
		//borderWidth: 0,
		borderRadius: 5,
		defaultSeriesType: 'line',
		ignoreHiddenSeries: true,
		//inverted: false,
		//shadow: false,
		spacingTop: 10,
		spacingRight: 10,
		spacingBottom: 15,
		spacingLeft: 10,
		style: {
			fontFamily: '"Lucida Grande", "Lucida Sans Unicode", Verdana, Arial, Helvetica, sans-serif', // default font
			fontSize: '12px'
		},
		backgroundColor: '#FFFFFF',
		//plotBackgroundColor: null,
		plotBorderColor: '#C0C0C0'
		//plotBorderWidth: 0,
		//plotShadow: false,
		//zoomType: ''
	},
	title: {
		text: 'Chart title',
		align: 'center',
		// floating: false,
		// margin: 15,
		// x: 0,
		// verticalAlign: 'top',
		y: 15,
		style: {
			color: '#3E576F',
			fontSize: '16px'
		}

	},
	subtitle: {
		text: '',
		align: 'center',
		// floating: false
		// x: 0,
		// verticalAlign: 'top',
		y: 30,
		style: {
			color: '#6D869F'
		}
	},

	plotOptions: {
		line: { // base series options
			allowPointSelect: false,
			showCheckbox: false,
			animation: {
				duration: 1000
			},
			// connectNulls: false, // docs
			//cursor: 'default',
			//clip: true,
			//dashStyle: null,
			//enableMouseTracking: true,
			events: {},
			//legendIndex: 0, // docs (+ pie points)
			lineWidth: 2,
			shadow: true,
			// stacking: null,
			marker: {
				enabled: true,
				//symbol: null,
				lineWidth: 0,
				radius: 4,
				lineColor: '#FFFFFF',
				//fillColor: null,
				states: { // states for a single point
					hover: {
						//radius: base + 2
					},
					select: {
						fillColor: '#FFFFFF',
						lineColor: '#000000',
						lineWidth: 2
					}
				}
			},
			point: {
				events: {}
			},
			dataLabels: merge(defaultLabelOptions, {
				enabled: false,
				y: -6,
				formatter: function() {
					return this.y;
				}
			}),
			cropThreshold: 300, // docs - draw points outside the plot area when the number of points is less than this
			//pointStart: 0,
			//pointInterval: 1,
			showInLegend: true,
			states: { // states for the entire series
				hover: {
					//enabled: false,
					//lineWidth: base + 1,
					marker: {
						// lineWidth: base + 1,
						// radius: base + 1
					}
				},
				select: {
					marker: {}
				}
			},
			stickyTracking: true
			// turboThreshold: 1000 // docs
			// zIndex: null
		}
	},
	labels: {
		//items: [],
		style: {
			//font: defaultFont,
			position: ABSOLUTE,
			color: '#3E576F'
		}
	},
	legend: {
		enabled: true,
		align: 'center',
		//floating: false,
		layout: 'horizontal',
		labelFormatter: function() {
			return this.name;
		},
		// lineHeight: 16,
		borderWidth: 1,
		borderColor: '#909090',
		borderRadius: 5,
		// margin: 10,
		// reversed: false,
		shadow: false,
		// backgroundColor: null,
		style: {
			padding: '5px'
		},
		itemStyle: {
			cursor: 'pointer',
			color: '#3E576F'
		},
		itemHoverStyle: {
			cursor: 'pointer',
			color: '#000000'
		},
		itemHiddenStyle: {
			color: '#C0C0C0'
		},
		itemCheckboxStyle: {
			position: ABSOLUTE,
			width: '13px', // for IE precision
			height: '13px'
		},
		// itemWidth: undefined,
		symbolWidth: 16,
		symbolPadding: 5,
		verticalAlign: 'bottom',
		// width: undefined,
		x: 0, // docs
		y: 0 // docs
	},

	loading: {
		hideDuration: 100,
		labelStyle: {
			fontWeight: 'bold',
			position: RELATIVE,
			top: '1em'
		},
		showDuration: 100,
		style: {
			position: ABSOLUTE,
			backgroundColor: 'white',
			opacity: 0.5,
			textAlign: 'center'
		}
	},

	tooltip: {
		enabled: true,
		//crosshairs: null,
		backgroundColor: 'rgba(255, 255, 255, .85)',
		borderWidth: 2,
		borderRadius: 5,
		//formatter: defaultFormatter,
		shadow: true,
		//shared: false,
		snap: hasTouch ? 25 : 10,
		style: {
			color: '#333333',
			fontSize: '12px',
			padding: '5px',
			whiteSpace: 'nowrap'
		}
	},

	toolbar: {
		itemStyle: {
			color: '#4572A7',
			cursor: 'pointer'
		}
	},

	credits: {
		enabled: true,
		text: 'Highstock Beta',
		href: 'http://www.highcharts.com',
		position: {
			align: 'right',
			x: -10,
			verticalAlign: 'bottom',
			y: -5
		},
		style: {
			cursor: 'pointer',
			color: '#909090',
			fontSize: '10px'
		}
	}
};

// Axis defaults
var defaultXAxisOptions =  {
	// allowDecimals: null,
	// alternateGridColor: null,
	// categories: [],
	dateTimeLabelFormats: hash(
		MILLISECOND, '%H:%M:%S.%L',
		SECOND, '%H:%M:%S',
		MINUTE, '%H:%M',
		HOUR, '%H:%M',
		DAY, '%e. %b',
		WEEK, '%e. %b',
		MONTH, '%b \'%y',
		YEAR, '%Y'
	),
	endOnTick: false,
	gridLineColor: '#C0C0C0',
	// gridLineDashStyle: 'solid', // docs
	// gridLineWidth: 0,
	// reversed: false,

	labels: defaultLabelOptions,
		// { step: null },
	lineColor: '#C0D0E0',
	lineWidth: 1,
	//linkedTo: null,
	max: null,
	min: null,
	minPadding: 0.01,
	maxPadding: 0.01,
	//maxZoom: null,
	minorGridLineColor: '#E0E0E0',
	// minorGridLineDashStyle: null,
	minorGridLineWidth: 1,
	minorTickColor: '#A0A0A0',
	//minorTickInterval: null,
	minorTickLength: 2,
	minorTickPosition: 'outside', // inside or outside
	//minorTickWidth: 0,
	//opposite: false,
	//offset: 0,
	//plotBands: [{
	//	events: {},
	//	zIndex: 1,
	//	labels: { align, x, verticalAlign, y, style, rotation, textAlign }
	//}],
	//plotLines: [{
	//	events: {}
	//  dashStyle: {}
	//	zIndex:
	//	labels: { align, x, verticalAlign, y, style, rotation, textAlign }
	//}],
	//reversed: false,
	// showFirstLabel: true,
	// showLastLabel: false,
	startOfWeek: 1,
	startOnTick: false,
	tickColor: '#C0D0E0',
	//tickInterval: null,
	tickLength: 5,
	tickmarkPlacement: 'between', // on or between
	tickPixelInterval: 100,
	tickPosition: 'outside',
	tickWidth: 1,
	title: {
		//text: null,
		align: 'middle', // low, middle or high
		//margin: 0 for horizontal, 10 for vertical axes,
		//rotation: 0,
		//side: 'outside',
		style: {
			color: '#6D869F',
			//font: defaultFont.replace('normal', 'bold')
			fontWeight: 'bold'
		}
		//x: 0,
		//y: 0
	},
	type: 'linear' // linear, logarithmic or datetime // docs
},

defaultYAxisOptions = merge(defaultXAxisOptions, {
	endOnTick: true,
	gridLineWidth: 1,
	tickPixelInterval: 72,
	showLastLabel: true,
	labels: {
		align: 'right',
		x: -8,
		y: 3
	},
	lineWidth: 0,
	maxPadding: 0.05,
	minPadding: 0.05,
	startOnTick: true,
	tickWidth: 0,
	title: {
		rotation: 270,
		text: 'Y-values'
	},
	stackLabels: {
		enabled: false,
		//align: dynamic,
		//y: dynamic,
		//x: dynamic,
		//verticalAlign: dynamic,
		//textAlign: dynamic,
		//rotation: 0,
		formatter: function() {
			return this.total;
		},
		style: defaultLabelOptions.style
	}
}),

defaultLeftAxisOptions = {
	labels: {
		align: 'right',
		x: -8,
		y: null // docs
	},
	title: {
		rotation: 270
	}
},
defaultRightAxisOptions = {
	labels: {
		align: 'left',
		x: 8,
		y: null // docs
	},
	title: {
		rotation: 90
	}
},
defaultBottomAxisOptions = { // horizontal axis
	labels: {
		align: 'center',
		x: 0,
		y: 14
		// staggerLines: null
	},
	title: {
		rotation: 0
	}
},
defaultTopAxisOptions = merge(defaultBottomAxisOptions, {
	labels: {
		y: -5
		// staggerLines: null
	}
});




// Series defaults
var defaultPlotOptions = defaultOptions.plotOptions,
	defaultSeriesOptions = defaultPlotOptions.line;
//defaultPlotOptions.line = merge(defaultSeriesOptions);
defaultPlotOptions.spline = merge(defaultSeriesOptions);
defaultPlotOptions.scatter = merge(defaultSeriesOptions, {
	lineWidth: 0,
	states: {
		hover: {
			lineWidth: 0
		}
	}
});
defaultPlotOptions.area = merge(defaultSeriesOptions, {
	threshold: 0
	// lineColor: null, // overrides color, but lets fillColor be unaltered
	// fillOpacity: 0.75,
	// fillColor: null

});
defaultPlotOptions.areaspline = merge(defaultPlotOptions.area);
defaultPlotOptions.column = merge(defaultSeriesOptions, {
	borderColor: '#FFFFFF',
	borderWidth: 1,
	borderRadius: 0,
	//colorByPoint: undefined,
	groupPadding: 0.2,
	marker: null, // point options are specified in the base options
	pointPadding: 0.1,
	//pointWidth: null,
	minPointLength: 0,
	cropThreshold: 50, // docs, when there are more points, they will not animate out of the chart on xAxis.setExtremes
	padXAxis: true,
	states: {
		hover: {
			brightness: 0.1,
			shadow: false
		},
		select: {
			color: '#C0C0C0',
			borderColor: '#000000',
			shadow: false
		}
	},
	dataLabels: {
		y: null,
		verticalAlign: null
	},
	threshold: 0
});
defaultPlotOptions.bar = merge(defaultPlotOptions.column, {
	dataLabels: {
		align: 'left',
		x: 5,
		y: 0
	}
});
defaultPlotOptions.pie = merge(defaultSeriesOptions, {
	//dragType: '', // n/a
	borderColor: '#FFFFFF',
	borderWidth: 1,
	center: ['50%', '50%'],
	colorByPoint: true, // always true for pies
	dataLabels: {
		// align: null,
		// connectorWidth: 1,
		// connectorColor: '#606060',
		// connectorPadding: 5,
		distance: 30,
		enabled: true,
		formatter: function() {
			return this.point.name;
		},
		y: 5
	},
	//innerSize: 0,
	legendType: 'point',
	marker: null, // point options are specified in the base options
	size: '75%',
	showInLegend: false,
	slicedOffset: 10,
	states: {
		hover: {
			brightness: 0.1,
			shadow: false
		}
	}

});

// set the default time methods
setTimeMethods();


/**
 * Handle color operations. The object methods are chainable.
 * @param {String} input The input color in either rbga or hex format
 */
var Color = function(input) {
	// declare variables
	var rgba = [], result;

	/**
	 * Parse the input color to rgba array
	 * @param {String} input
	 */
	function init(input) {

		// rgba
		result = /rgba\(\s*([0-9]{1,3})\s*,\s*([0-9]{1,3})\s*,\s*([0-9]{1,3})\s*,\s*([0-9]?(?:\.[0-9]+)?)\s*\)/.exec(input);
		if (result) {
			rgba = [pInt(result[1]), pInt(result[2]), pInt(result[3]), parseFloat(result[4], 10)];
		}

		// hex
		else {
			result = /#([a-fA-F0-9]{2})([a-fA-F0-9]{2})([a-fA-F0-9]{2})/.exec(input);
			if (result) {
				rgba = [pInt(result[1], 16), pInt(result[2], 16), pInt(result[3], 16), 1];
			}
		}

	}
	/**
	 * Return the color a specified format
	 * @param {String} format
	 */
	function get(format) {
		var ret;

		// it's NaN if gradient colors on a column chart
		if (rgba &amp;&amp; !isNaN(rgba[0])) {
			if (format === 'rgb') {
				ret = 'rgb('+ rgba[0] +','+ rgba[1] +','+ rgba[2] +')';
			} else if (format === 'a') {
				ret = rgba[3];
			} else {
				ret = 'rgba('+ rgba.join(',') +')';
			}
		} else {
			ret = input;
		}
		return ret;
	}

	/**
	 * Brighten the color
	 * @param {Number} alpha
	 */
	function brighten(alpha) {
		if (isNumber(alpha) &amp;&amp; alpha !== 0) {
			var i;
			for (i = 0; i &lt; 3; i++) {
				rgba[i] += pInt(alpha * 255);

				if (rgba[i] &lt; 0) {
					rgba[i] = 0;
				}
				if (rgba[i] &gt; 255) {
					rgba[i] = 255;
				}
			}
		}
		return this;
	}
	/**
	 * Set the color's opacity to a given alpha value
	 * @param {Number} alpha
	 */
	function setOpacity(alpha) {
		rgba[3] = alpha;
		return this;
	}

	// initialize: parse the input
	init(input);

	// public methods
	return {
		get: get,
		brighten: brighten,
		setOpacity: setOpacity
	};
};


/**
 * A wrapper object for SVG elements
 */
function SVGElement () {}

SVGElement.prototype = {
	/**
	 * Initialize the SVG renderer
	 * @param {Object} renderer
	 * @param {String} nodeName
	 */
	init: function(renderer, nodeName) {
		this.element = doc.createElementNS(SVG_NS, nodeName);
		this.renderer = renderer;
	},
	/**
	 * Animate a given attribute
	 * @param {Object} params
	 * @param {Number} options The same options as in jQuery animation
	 * @param {Function} complete Function to perform at the end of animation
	 */
	animate: function(params, options, complete) {
		var animOptions = pick(options, globalAnimation, true);
		if (animOptions) {
			animOptions = merge(animOptions);
			if (complete) { // allows using a callback with the global animation without overwriting it
				animOptions.complete = complete;
			}
			animate(this, params, animOptions);
		} else {
			this.attr(params);
			if (complete) {
				complete();
			}
		}
	},
	/**
	 * Set or get a given attribute
	 * @param {Object|String} hash
	 * @param {Mixed|Undefined} val
	 */
	attr: function(hash, val) {
		var wrapper = this,
			key,
			value,
			result,
			i,
			child,
			element = wrapper.element,
			nodeName = element.nodeName,
			renderer = wrapper.renderer,
			skipAttr,
			shadows = wrapper.shadows,
			hasSetSymbolSize,
			ret = wrapper;

		// single key-value pair
		if (isString(hash) &amp;&amp; defined(val)) {
			key = hash;
			hash = {};
			hash[key] = val;
		}

		// used as a getter: first argument is a string, second is undefined
		if (isString(hash)) {
			key = hash;
			if (nodeName === 'circle') {
				key = { x: 'cx', y: 'cy' }[key] || key;
			} else if (key === 'strokeWidth') {
				key = 'stroke-width';
			}
			ret = attr(element, key) || wrapper[key] || 0;

			if (key !== 'd' &amp;&amp; key !== 'visibility') { // 'd' is string in animation step
				ret = parseFloat(ret);
			}

		// setter
		} else {

			for (key in hash) {
				skipAttr = false; // reset
				value = hash[key];

				fireEvent(wrapper, 'setAttr', { key: key, value: value }, function(e) {
					result = e.result;
					if (defined(result) &amp;&amp; result !== false) {
						value = result;
					}
					// paths
					if (key === 'd') {
						if (value &amp;&amp; value.join) { // join path
							value = value.join(' ');
						}
						if (/(NaN| {2}|^$)/.test(value)) {
							value = 'M 0 0';
						}
						wrapper.d = value; // shortcut for animations

					// update child tspans x values
					} else if (key === 'x' &amp;&amp; nodeName === 'text') {
						for (i = 0; i &lt; element.childNodes.length; i++ ) {
							child = element.childNodes[i];
							// if the x values are equal, the tspan represents a linebreak
							if (attr(child, 'x') === attr(element, 'x')) {
								//child.setAttribute('x', value);
								attr(child, 'x', value);
							}
						}

						if (wrapper.rotation) {
							attr(element, 'transform', 'rotate('+ wrapper.rotation +' '+ value +' '+
								pInt(hash.y || attr(element, 'y')) +')');
						}

					// apply gradients
					} else if (key === 'fill') {
						value = renderer.color(value, element, key);

					// circle x and y
					} else if (nodeName === 'circle' &amp;&amp; (key === 'x' || key === 'y')) {
						key = { x: 'cx', y: 'cy' }[key] || key;

					// rectangle border radius
					} else if (nodeName === 'rect' &amp;&amp; key === 'r') {
						attr(element, {
							rx: value,
							ry: value
						});
						skipAttr = true;

					// translation and text rotation
					} else if (key === 'translateX' || key === 'translateY' || key === 'rotation' || key === 'verticalAlign') {
						wrapper[key] = value;
						wrapper.updateTransform();
						skipAttr = true;

					// apply opacity as subnode (required by legacy WebKit and Batik)
					} else if (key === 'stroke') {
						value = renderer.color(value, element, key);

					// emulate VML's dashstyle implementation
					} else if (key === 'dashstyle') {
						key = 'stroke-dasharray';
						value = value &amp;&amp; value.toLowerCase();
						if (value === 'solid') {
							value = NONE;
						} else if (value) {
							value = value
								.replace('shortdashdotdot', '3,1,1,1,1,1,')
								.replace('shortdashdot', '3,1,1,1')
								.replace('shortdot', '1,1,')
								.replace('shortdash', '3,1,')
								.replace('longdash', '8,3,')
								.replace(/dot/g, '1,3,')
								.replace('dash', '4,3,')
								.replace(/,$/, '')
								.split(','); // ending comma

							i = value.length;
							while (i--) {
								value[i] = pInt(value[i]) * hash['stroke-width'];
							}
							value = value.join(',');
						}

					// special
					} else if (key === 'isTracker') {
						wrapper[key] = value;

					// IE9/MooTools combo: MooTools returns objects instead of numbers and IE9 Beta 2
					// is unable to cast them. Test again with final IE9.
					} else if (key === 'width') {
						value = pInt(value);

					// Text alignment
					} else if (key === 'align') {
						key = 'text-anchor';
						value = { left: 'start', center: 'middle', right: 'end' }[value];
					}



					// jQuery animate changes case
					if (key === 'strokeWidth') {
						key = 'stroke-width';
					}

					// Chrome/Win &lt; 6 bug (http://code.google.com/p/chromium/issues/detail?id=15461)
					if (isWebKit &amp;&amp; key === 'stroke-width' &amp;&amp; value === 0) {
						value = 0.000001;
					}

					// symbols
					if (wrapper.symbolName &amp;&amp; /^(x|y|r|start|end|innerR)/.test(key)) {


						if (!hasSetSymbolSize) {
							wrapper.symbolAttr(hash);
							hasSetSymbolSize = true;
						}
						skipAttr = true;
					}

					// let the shadow follow the main element
					if (shadows &amp;&amp; /^(width|height|visibility|x|y|d|transform)$/.test(key)) {
						i = shadows.length;
						while (i--) {
							attr(shadows[i], key, value);
						}
					}

					// validate heights
					if ((key === 'width' || key === 'height') &amp;&amp; nodeName === 'rect' &amp;&amp; value &lt; 0) {
						value = 0;
					}




					if (key === 'text') {
						// only one node allowed
						wrapper.textStr = value;
						if (wrapper.added) {
							renderer.buildText(wrapper);
						}
					} else if (!skipAttr) {
						//fireEvent(this, 'setAttr', { key: key, value: value }, function() {
							attr(element, key, value);
						//});
					}
					if (e.callback) {
						e.callback();
					}
				});

			}

		}
		return ret;
	},

	/**
	 * If one of the symbol size affecting parameters are changed,
	 * check all the others only once for each call to an element's
	 * .attr() method
	 * @param {Object} hash
	 */
	symbolAttr: function(hash) {
		var wrapper = this;

		each(['x', 'y', 'r', 'start', 'end', 'width', 'height', 'innerR', 'anchorX', 'anchorY'], function(key) {
			wrapper[key] = pick(hash[key], wrapper[key]);
		});

		wrapper.attr({
			d: wrapper.renderer.symbols[wrapper.symbolName](wrapper.x, wrapper.y, wrapper.width, wrapper.height, wrapper)
		});
	},

	/**
	 * Apply a clipping path to this object
	 * @param {String} id
	 */
	clip: function(clipRect) {
		return this.attr('clip-path', 'url('+ this.renderer.url +'#'+ clipRect.id +')');
	},

	/**
	 * Calculate the coordinates needed for drawing a rectangle crisply and return the
	 * calculated attributes
	 * @param {Number} strokeWidth
	 * @param {Number} x
	 * @param {Number} y
	 * @param {Number} width
	 * @param {Number} height
	 */
	crisp: function(strokeWidth, x, y, width, height) {

		var wrapper = this,
			element = wrapper.element,
			key,
			attribs = {},
			values = {},
			normalizer;

		strokeWidth = strokeWidth || wrapper.strokeWidth || (wrapper.attr &amp;&amp; wrapper.attr('stroke-width')) || 0;
		normalizer = strokeWidth % 2 / 2;

		// normalize for crisp edges
		values.x = mathFloor(x || wrapper.x || 0) + normalizer;
		values.y = mathFloor(y || wrapper.y || 0) + normalizer;
		values.width = mathFloor((width || wrapper.width || 0) - 2 * normalizer);
		values.height = mathFloor((height || wrapper.height || 0) - 2 * normalizer);
		values.strokeWidth = strokeWidth;

		for (key in values) {
			if (wrapper[key] !== values[key]) { // only set attribute if changed
				wrapper[key] = attribs[key] = values[key];
			}
		}

		return attribs;
	},

	/**
	 * Set styles for the element
	 * @param {Object} styles
	 */
	css: function(styles) {
		var elemWrapper = this,
			elem = elemWrapper.element,
			textWidth = styles &amp;&amp; styles.width &amp;&amp; elem.nodeName === 'text',
			n,
			serializedCss,
			hyphenate = function(a, b){ return '-'+ b.toLowerCase(); };

		// convert legacy
		if (styles &amp;&amp; styles.color) {
			styles.fill = styles.color;
		}
		
		// merge the new styles with the old ones
		styles = extend(
			elemWrapper.styles,
			styles
		);

		// store object
		elemWrapper.styles = styles;

		// serialize and set style attribute
		if (isIE &amp;&amp; !hasSVG) { // legacy IE doesn't support setting style attribute
			if (textWidth) {
				delete styles.width;
			}
			css(elemWrapper.element, styles);
		} else {
			for (n in styles) {
				serializedCss += n.replace(/([A-Z])/g, hyphenate) + ':'+ styles[n] + ';';
			} 
			elemWrapper.attr({
				style: serializedCss
			});
		}


		// re-build text
		if (textWidth &amp;&amp; elemWrapper.added) {
			elemWrapper.renderer.buildText(elemWrapper);
		}

		return elemWrapper;
	},

	/**
	 * Add an event listener
	 * @param {String} eventType
	 * @param {Function} handler
	 */
	on: function(eventType, handler) {
		var fn = handler;
		// touch
		if (hasTouch &amp;&amp; eventType === 'click') {
			eventType = 'touchstart';
			fn = function(e) {
				e.preventDefault();
				handler();
			};
		}
		// simplest possible event model for internal use
		this.element['on'+ eventType] = fn;
		return this;
	},


	/**
	 * Move an object and its children by x and y values
	 * @param {Number} x
	 * @param {Number} y
	 */
	translate: function(x, y) {
		return this.attr({
			translateX: x,
			translateY: y
		});
	},

	/**
	 * Invert a group, rotate and flip
	 */
	invert: function() {
		var wrapper = this;
		wrapper.inverted = true;
		wrapper.updateTransform();
		return wrapper;
	},

	/**
	 * Private method to update the transform attribute based on internal
	 * properties
	 */
	updateTransform: function() {
		var wrapper = this,
			translateX = wrapper.translateX || 0,
			translateY = wrapper.translateY || 0,
			inverted = wrapper.inverted,
			rotation = wrapper.rotation,
			shadows = wrapper.shadows,
			transform = [];

		// flipping affects translate as adjustment for flipping around the group's axis
		if (inverted) {
			translateX += wrapper.attr('width');
			translateY += wrapper.attr('height');
		}

		// apply translate
		if (translateX || translateY) {
			transform.push('translate('+ translateX +','+ translateY +')');
		}

		// apply rotation
		if (inverted) {
			transform.push('rotate(90) scale(-1,1)');
		} else if (rotation) { // text rotation
			transform.push('rotate('+ rotation +' '+ wrapper.x +' '+ wrapper.y +')');
		}

		if (transform.length) {
			attr(wrapper.element, 'transform', transform.join(' '));
			if (shadows) { // in label/tooltip
				each(shadows, function(shadow) {
					attr(shadow, 'transform', 'translate('+ (translateX + 1) +','+ (translateY + 1) +')');
				});
			}
		}
	},
	/**
	 * Bring the element to the front
	 */
	toFront: function() {
		var element = this.element;
		element.parentNode.appendChild(element);
		return this;
	},


	/**
	 * Break down alignment options like align, verticalAlign, x and y
	 * to x and y relative to the chart.
	 *
	 * @param {Object} alignOptions
	 * @param {Boolean} alignByTranslate
	 * @param {Object} box The box to align to, needs a width and height
	 *
	 */
	align: function(alignOptions, alignByTranslate, box) {
		var elemWrapper = this;

		if (!alignOptions) { // called on resize
			alignOptions = elemWrapper.alignOptions;
			alignByTranslate = elemWrapper.alignByTranslate;
		} else { // first call on instanciate
			elemWrapper.alignOptions = alignOptions;
			elemWrapper.alignByTranslate = alignByTranslate;
			if (!box) { // boxes other than renderer handle this internally
				elemWrapper.renderer.alignedObjects.push(elemWrapper);
			}
		}

		box = pick(box, elemWrapper.renderer);

		var align = alignOptions.align,
			vAlign = alignOptions.verticalAlign,
			x = (box.x || 0) + (alignOptions.x || 0), // default: left align
			y = (box.y || 0) + (alignOptions.y || 0), // default: top align
			attribs = {};


		// align
		if (/^(right|center)$/.test(align)) {
			x += (box.width - (alignOptions.width || 0) ) /
					{ right: 1, center: 2 }[align];
		}
		attribs[alignByTranslate ? 'translateX' : 'x'] = mathRound(x);


		// vertical align
		if (/^(bottom|middle)$/.test(vAlign)) {
			y += (box.height - (alignOptions.height || 0)) /
					({ bottom: 1, middle: 2 }[vAlign] || 1);

		}
		attribs[alignByTranslate ? 'translateY' : 'y'] = mathRound(y);

		// animate only if already placed
		elemWrapper[elemWrapper.placed ? 'animate' : 'attr'](attribs);
		elemWrapper.placed = true;
		elemWrapper.alignAttr = attribs;

		return elemWrapper;
	},

	/**
	 * Get the bounding box (width, height, x and y) for the element
	 */
	getBBox: function() {
		var bBox,
			width,
			height,
			rotation = this.rotation,
			rad = rotation * deg2rad;

		try { // fails in Firefox if the container has display: none
			// use extend because IE9 is not allowed to change width and height in case
			// of rotation (below)
			bBox = extend({}, this.element.getBBox());
		} catch(e) {
			bBox = { width: 0, height: 0 };
		}
		width = bBox.width;
		height = bBox.height;

		// adjust for rotated text
		if (rotation) {
			bBox.width = mathAbs(height * mathSin(rad)) + mathAbs(width * mathCos(rad));
			bBox.height = mathAbs(height * mathCos(rad)) + mathAbs(width * mathSin(rad));
		}

		return bBox;
	},

	/**
	 * Show the element
	 */
	show: function() {
		return this.attr({ visibility: VISIBLE });
	},

	/**
	 * Hide the element
	 */
	hide: function() {
		return this.attr({ visibility: HIDDEN });
	},

	/**
	 * Add the element
	 * @param {Object|Undefined} parent Can be an element, an element wrapper or undefined
	 *    to append the element to the renderer.box.
	 */
	add: function(parent) {

		var renderer = this.renderer,
			parentWrapper = parent || renderer,
			parentNode = parentWrapper.element || renderer.box,
			childNodes = parentNode.childNodes,
			element = this.element,
			zIndex = attr(element, 'zIndex'),
			otherElement,
			otherZIndex,
			i,
			inserted;

		// mark as inverted
		this.parentInverted = parent &amp;&amp; parent.inverted;

		// build formatted text
		if (this.textStr !== undefined) {
			renderer.buildText(this);
		}

		// mark the container as having z indexed children
		if (zIndex) {
			parentWrapper.handleZ = true;
			zIndex = pInt(zIndex);
		}

		// insert according to this and other elements' zIndex
		if (parentWrapper.handleZ) { // this element or any of its siblings has a z index
			for (i = 0; i &lt; childNodes.length; i++) {
				otherElement = childNodes[i];
				otherZIndex = attr(otherElement, 'zIndex');
				if (otherElement !== element &amp;&amp; (
						// insert before the first element with a higher zIndex
						pInt(otherZIndex) &gt; zIndex ||
						// if no zIndex given, insert before the first element with a zIndex
						(!defined(zIndex) &amp;&amp; defined(otherZIndex))

						)) {
					parentNode.insertBefore(element, otherElement);
					inserted = true;
					break;
				}
			}
		}

		// default: append at the end
		if (!inserted) {
			parentNode.appendChild(element);
		}

		// mark as added
		this.added = true;

		// fire an event for internal hooks
		fireEvent(this, 'add');

		return this;
	},

	/**
	 * Destroy the element and element wrapper
	 */
	destroy: function() {
		var wrapper = this,
			element = wrapper.element || {},
			shadows = wrapper.shadows,
			box = wrapper.box,
			parentNode = element.parentNode,
			key;

		// remove events
		element.onclick = element.onmouseout = element.onmouseover = element.onmousemove = null;
		stop(wrapper); // stop running animations

		// remove element
		if (parentNode) {
			parentNode.removeChild(element);
		}

		// destroy shadows
		if (shadows) {
			each(shadows, function(shadow) {
				parentNode = shadow.parentNode;
				if (parentNode) { // the entire chart HTML can be overwritten
					parentNode.removeChild(shadow);
				}
			});
		}

		// destroy label box
		if (box) {
			box.destroy();
		}

		// remove from alignObjects
		erase(wrapper.renderer.alignedObjects, wrapper);

		for (key in wrapper) {
			delete wrapper[key];
		}

		return null;
	},

	/**
	 * Empty a group element
	 */
	empty: function() {
		var element = this.element,
			childNodes = element.childNodes,
			i = childNodes.length;

		while (i--) {
			element.removeChild(childNodes[i]);
		}
	},

	/**
	 * Add a shadow to the element. Must be done after the element is added to the DOM
	 * @param {Boolean} apply
	 */
	shadow: function(apply, group) {
		var shadows = [],
			i,
			shadow,
			element = this.element,

			// compensate for inverted plot area
			transform = this.parentInverted ? '(-1,-1)' : '(1,1)';


		if (apply) {
			for (i = 1; i &lt;= 3; i++) {
				shadow = element.cloneNode(0);
				attr(shadow, {
					'isShadow': 'true',
					'stroke': 'rgb(0, 0, 0)',
					'stroke-opacity': 0.05 * i,
					'stroke-width': 7 - 2 * i,
					'transform': 'translate'+ transform,
					'fill': NONE
				});

				if (group) {
					group.element.appendChild(shadow);
				} else {
					element.parentNode.insertBefore(shadow, element);
				}

				shadows.push(shadow);
			}

			this.shadows = shadows;
		}
		return this;

	}
};


/**
 * The default SVG renderer
 */
var SVGRenderer = function() {
	this.init.apply(this, arguments);
};
SVGRenderer.prototype = {

	Element: SVGElement,

	/**
	 * Initialize the SVGRenderer
	 * @param {Object} container
	 * @param {Number} width
	 * @param {Number} height
	 * @param {Boolean} forExport
	 */
	init: function(container, width, height, forExport) {
		var renderer = this,
			loc = location,
			boxWrapper;

		boxWrapper = renderer.createElement('svg')
			.attr({
				xmlns: SVG_NS,
				version: '1.1'
			});
		container.appendChild(boxWrapper.element);

		// object properties
		renderer.box = boxWrapper.element;
		renderer.boxWrapper = boxWrapper;
		renderer.alignedObjects = [];
		renderer.url = isIE ? '' : loc.href.replace(/#.*?$/, ''); // page url used for internal references
		renderer.defs = this.createElement('defs').add();
		renderer.forExport = forExport;

		renderer.setSize(width, height, false);

	},


	/**
	 * Create a wrapper for an SVG element
	 * @param {Object} nodeName
	 */
	createElement: function(nodeName) {
		var wrapper = new this.Element();
		wrapper.init(this, nodeName);
		return wrapper;
	},


	/**
	 * Parse a simple HTML string into SVG tspans
	 *
	 * @param {Object} textNode The parent text SVG node
	 */
	buildText: function(wrapper) {
		var textNode = wrapper.element,
			lines = pick(wrapper.textStr, '').toString()
				.replace(/&lt;(b|strong)&gt;/g, '<span style="font-weight: bold;">')
				.replace(/&lt;(i|em)&gt;/g, '<span style="font-style: italic;">')
				.replace(/<a g,=""><span') .replace(="">&lt;\/(b|strong|i|em|a)&gt;/g, '</span')></a></span><a g,="">')
				.split(/<br.*?>/g),
			childNodes = textNode.childNodes,
			styleRegex = /style="([^"]+)"/,
			hrefRegex = /href="([^"]+)"/,
			parentX = attr(textNode, 'x'),
			textStyles = wrapper.styles,
			reverse = isFirefox &amp;&amp; textStyles &amp;&amp; textStyles.HcDirection === 'rtl' &amp;&amp;
				!this.forExport &amp;&amp; pInt(userAgent.split('Firefox/')[1]) &lt; 4, // issue #38
			arr,
			width = textStyles &amp;&amp; pInt(textStyles.width),
			textLineHeight = textStyles &amp;&amp; textStyles.lineHeight,
			lastLine,
			GET_COMPUTED_STYLE = 'getComputedStyle',
			i = childNodes.length;

		// remove old text
		while (i--) {
			textNode.removeChild(childNodes[i]);
		}

		if (width &amp;&amp; !wrapper.added) {
			this.box.appendChild(textNode); // attach it to the DOM to read offset width
		}

		each(lines, function(line, lineNo) {
			var spans, spanNo = 0, lineHeight;

			line = line.replace(/<span g,="" |||=""><span').replace(>&lt;\/span&gt;/g, '</span').replace(></span>|||');
			spans = line.split('|||');

			each(spans, function (span) {
				if (span !== '' || spans.length === 1) {
					var attributes = {},
						tspan = doc.createElementNS(SVG_NS, 'tspan');
					if (styleRegex.test(span)) {
						attr(
							tspan,
							'style',
							span.match(styleRegex)[1].replace(/(;| |^)color([ :])/, '$1fill$2')
						);
					}
					if (hrefRegex.test(span)) {
						attr(tspan, 'onclick', 'location.href=\"'+ span.match(hrefRegex)[1] +'\"');
						css(tspan, { cursor: 'pointer' });
					}

					span = (span.replace(/&lt;(.|\n)*?&gt;/g, '') || ' ')
						.replace(/&lt;/g, '&lt;')
						.replace(/&gt;/g, '&gt;');

					// issue #38 workaround.
					if (reverse) {
						arr = [];
						i = span.length;
						while (i--) {
							arr.push(span.charAt(i));
						}
						span = arr.join('');
					}

					// add the text node
					tspan.appendChild(doc.createTextNode(span));

					if (!spanNo) { // first span in a line, align it to the left
						attributes.x = parentX;
					} else {
						// Firefox ignores spaces at the front or end of the tspan
						attributes.dx = 3; // space
					}

					// first span on subsequent line, add the line height
					if (!spanNo) {
						if (lineNo) {

							// allow getting the right offset height in exporting in IE
							if (!hasSVG &amp;&amp; wrapper.renderer.forExport) {
								css(tspan, { display: 'block' });
							}

							// Webkit and opera sometimes return 'normal' as the line height. In that
							// case, webkit uses offsetHeight, while Opera falls back to 18
							lineHeight = win[GET_COMPUTED_STYLE] &amp;&amp;
								win[GET_COMPUTED_STYLE](lastLine, null).getPropertyValue('line-height');

							if (!lineHeight || isNaN(lineHeight)) {
								lineHeight = textLineHeight || lastLine.offsetHeight || 18;
							}
							attr(tspan, 'dy', lineHeight);
						}
						lastLine = tspan; // record for use in next line
					}

					// add attributes
					attr(tspan, attributes);

					// append it
					textNode.appendChild(tspan);

					spanNo++;

					// check width and apply soft breaks
					if (width) {
						var words = span.replace(/-/g, '- ').split(' '),
							tooLong,
							actualWidth,
							rest = [];

						while (words.length || rest.length) {
							actualWidth = textNode.getBBox().width;
							tooLong = actualWidth &gt; width;
							if (!tooLong || words.length === 1) { // new line needed
								words = rest;
								rest = [];
								if (words.length) {
									tspan = doc.createElementNS(SVG_NS, 'tspan');
									attr(tspan, {
										dy: textLineHeight || 16,
										x: parentX
									});
									textNode.appendChild(tspan);

									if (actualWidth &gt; width) { // a single word is pressing it out
										width = actualWidth;
									}
								}
							} else { // append to existing line tspan
								tspan.removeChild(tspan.firstChild);
								rest.unshift(words.pop());
							}
							if (words.length) {
								tspan.appendChild(doc.createTextNode(words.join(' ').replace(/- /g, '-')));
							}
						}
					}
				}
			});
		});


	},

	/**
	 * Create a button with preset states
	 * @param {String} text
	 * @param {Number} x
	 * @param {Number} y
	 * @param {Function} callback
	 * @param {Object} normalState
	 * @param {Object} hoverState
	 * @param {Object} pressedState
	 */
	button: function(text, x, y, callback, normalState, hoverState, pressedState) {
		var label = this.label(text, x, y),
			curState = 0,
			stateOptions,
			stateStyle,
			normalStyle,
			hoverStyle,
			pressedStyle,
			STYLE = 'style';
			
		// prepare the attributes
		normalState = merge(hash(
			STROKE_WIDTH, 1,
			STROKE, '#999',
			FILL, hash(
				LINEAR_GRADIENT, [0, 0, 0, 14],
				STOPS, [
					[0, '#FFF'],
					[1, '#DDD']
				]
			),
			'r', 3,
			'padding', 3,
			STYLE, hash(
				'color', 'black'
			)
		), normalState);
		normalStyle = normalState[STYLE];
		delete normalState[STYLE];

		hoverState = merge(normalState, hash(
			STROKE, '#68A',
			FILL, hash(
				LINEAR_GRADIENT, [0, 0, 0, 14],
				STOPS, [
					[0, '#FFF'],
					[1, '#ACF']
				]
			)
		), hoverState);
		hoverStyle = hoverState[STYLE];
		delete hoverState[STYLE];

		pressedState = merge(normalState, hash(
			STROKE, '#68A',
			FILL, hash(
				LINEAR_GRADIENT, [0, 0, 0, 14],
				STOPS, [
					[0, '#9BD'],
					[1, '#CDF']
				]
			)
		), pressedState);
		pressedStyle = pressedState[STYLE];
		delete pressedState[STYLE];

		// add the events
		addEvent(label.element, 'mouseenter', function() {
			label.attr(hoverState)
				.css(hoverStyle);
		});
		addEvent(label.element, 'mouseleave', function() {
			stateOptions = [normalState, hoverState, pressedState][curState];
			stateStyle = [normalStyle, hoverStyle, pressedStyle][curState];
			label.attr(stateOptions)
				.css(stateStyle);
		});

		label.setState = function(state) {
			curState = state;
			if (!state) {
				label.attr(normalState)
					.css(normalStyle);
			} else if (state === 2) {
				label.attr(pressedState)
					.css(pressedStyle);
			}
		};
		
		return label
			.on('click', function() {
				callback.call(label);
			})
			.attr(normalState)
			.css(extend({ cursor: 'default' }, normalStyle));

	},

	/**
	 * Make a straight line crisper by not spilling out to neighbour pixels
	 * @param {Array} points
	 * @param {Number} width
	 */
	crispLine: function(points, width) {
		// points format: [M, 0, 0, L, 100, 0]
		// normalize to a crisp line
		if (points[1] === points[4]) {
			points[1] = points[4] = mathRound(points[1]) + (width % 2 / 2);
		}
		if (points[2] === points[5]) {
			points[2] = points[5] = mathRound(points[2]) + (width % 2 / 2);
		}
		return points;
	},


	/**
	 * Draw a path
	 * @param {Array} path An SVG path in array form
	 */
	path: function (path) {
		return this.createElement('path').attr({
			d: path,
			fill: NONE
		});
	},

	/**
	 * Draw and return an SVG circle
	 * @param {Number} x The x position
	 * @param {Number} y The y position
	 * @param {Number} r The radius
	 */
	circle: function (x, y, r) {
		var attr = isObject(x) ?
			x :
			{
				x: x,
				y: y,
				r: r
			};

		return this.createElement('circle').attr(attr);
	},

	/**
	 * Draw and return an arc
	 * @param {Number} x X position
	 * @param {Number} y Y position
	 * @param {Number} r Radius
	 * @param {Number} innerR Inner radius like used in donut charts
	 * @param {Number} start Starting angle
	 * @param {Number} end Ending angle
	 */
	arc: function (x, y, r, innerR, start, end) {
		// arcs are defined as symbols for the ability to set
		// attributes in attr and animate

		if (isObject(x)) {
			y = x.y;
			r = x.r;
			innerR = x.innerR;
			start = x.start;
			end = x.end;
			x = x.x;
		}
		return this.symbol('arc', x || 0, y || 0, r || 0, r || 0, {
			innerR: innerR || 0,
			start: start || 0,
			end: end || 0
		});
	},

	/**
	 * Draw and return a rectangle
	 * @param {Number} x Left position
	 * @param {Number} y Top position
	 * @param {Number} width
	 * @param {Number} height
	 * @param {Number} r Border corner radius
	 * @param {Number} strokeWidth A stroke width can be supplied to allow crisp drawing
	 */
	rect: function (x, y, width, height, r, strokeWidth) {
		if (isObject(x)) {
			y = x.y;
			width = x.width;
			height = x.height;
			r = x.r;
			strokeWidth = x.strokeWidth;
			x = x.x;
		}
		var wrapper = this.createElement('rect').attr({
			rx: r,
			ry: r,
			fill: NONE
		});

		return wrapper.attr(wrapper.crisp(strokeWidth, x, y, mathMax(width, 0), mathMax(height, 0)));
	},

	/**
	 * Resize the box and re-align all aligned elements
	 * @param {Object} width
	 * @param {Object} height
	 * @param {Boolean} animate
	 *
	 */
	setSize: function(width, height, animate) {
		var renderer = this,
			alignedObjects = renderer.alignedObjects,
			i = alignedObjects.length;

		renderer.width = width;
		renderer.height = height;

		renderer.boxWrapper[pick(animate, true) ? 'animate' : 'attr']({
			width: width,
			height: height
		});

		while (i--) {
			alignedObjects[i].align();
		}
	},

	/**
	 * Create a group
	 * @param {String} name The group will be given a class name of 'highcharts-{name}'.
	 *     This can be used for styling and scripting.
	 */
	g: function(name) {
		return this.createElement('g').attr(
			defined(name) &amp;&amp; { 'class': PREFIX + name }
		);
	},

	/**
	 * Display an image
	 * @param {String} src
	 * @param {Number} x
	 * @param {Number} y
	 * @param {Number} width
	 * @param {Number} height
	 */
	image: function(src, x, y, width, height) {
		var attribs = {
				preserveAspectRatio: NONE
			},
			elemWrapper;

		// optional properties
		if (arguments.length &gt; 1) {
			extend(attribs, {
				x: x,
				y: y,
				width: width,
				height: height
			});
		}

		elemWrapper = this.createElement('image').attr(attribs);

		// set the href in the xlink namespace
		if (elemWrapper.element.setAttributeNS) {
			elemWrapper.element.setAttributeNS('http://www.w3.org/1999/xlink',
				'href', src);
		} else {
			// could be exporting in IE
			// using href throws "not supported" in ie7 and under, requries regex shim to fix later
			elemWrapper.element.setAttribute('hc-svg-href', src);
	}

		return elemWrapper;
	},

	/**
	 * Draw a symbol out of pre-defined shape paths from the namespace 'symbol' object.
	 *
	 * @param {Object} symbol
	 * @param {Object} x
	 * @param {Object} y
	 * @param {Object} radius
	 * @param {Object} options
	 */
	symbol: function(symbol, x, y, width, height, options) {

		var obj,

			// get the symbol definition function
			symbolFn = this.symbols[symbol],

			// check if there's a path defined for this symbol
			path = symbolFn &amp;&amp; symbolFn(
				mathRound(x),
				mathRound(y),
				width,
				height,
				options
			),

			imageRegex = /^url\((.*?)\)$/,
			imageSrc,
			imageSize;

		if (path) {

			obj = this.path(path);
			// expando properties for use in animate and attr
			extend(obj, {
				symbolName: symbol,
				x: x,
				y: y,
				width: width,
				height: height
			});
			if (options) {
				extend(obj, options);
			}


		// image symbols
		} else if (imageRegex.test(symbol)) {

			var centerImage = function(img, size) {
				img.attr({
					width: size[0],
					height: size[1]
				}).translate(
					-mathRound(size[0] / 2),
					-mathRound(size[1] / 2)
				);
			};

			imageSrc = symbol.match(imageRegex)[1];
			imageSize = symbolSizes[imageSrc];

			// create the image synchronously, add attribs async
			obj = this.image(imageSrc)
				.attr({
					x: x,
					y: y
				});

			if (imageSize) {
				centerImage(obj, imageSize);
			} else {
				// initialize image to be 0 size so export will still function if there's no cached sizes
				obj.attr({ width: 0, height: 0 });

				// create a dummy JavaScript image to get the width and height
				createElement('img', {
					onload: function() {
						var img = this;

						centerImage(obj, symbolSizes[imageSrc] = [img.width, img.height]);
					},
					src: imageSrc
				});
			}
		}

		return obj;
	},

	/**
	 * An extendable collection of functions for defining symbol paths.
	 */
	symbols: {
		'circle': function (x, y, w, h) {
			var cpw = 0.166 * w;
			return [
				M, x + w / 2, y,
				'C', x + w + cpw, y, x + w + cpw, y + h, x + w / 2, y + h,
				'C', x - cpw, y + h, x - cpw, y, x + w / 2, y,
				'Z'
			];
		},

		'square': function (x, y, w, h) {
			return [
				M, x, y,
				L, x + w, y,
				x + w, y + h,
				x, y + h,
				'Z'
			];
		},

		'triangle': function (x, y, w, h) {
			return [
				M, x + w / 2, y,
				L, x + w, y + h,
				x, y + h,
				'Z'
			];
		},

		'triangle-down': function (x, y, w, h) {
			return [
				M, x, y,
				L, x + w, y,
				x + w / 2, y + h,
				'Z'
			];
		},
		'diamond': function (x, y, w, h) {
			return [
				M, x + w / 2, y,
				L, x + w, y + h / 2,
				x + w / 2, y + h,
				x, y + h / 2,
				'Z'
			];
		},
		'arc': function (x, y, w, h, options) {
			var start = options.start,
				radius = w,
				end = options.end - 0.000001, // to prevent cos and sin of start and end from becoming equal on 360 arcs
				innerRadius = options.innerR,
				cosStart = mathCos(start),
				sinStart = mathSin(start),
				cosEnd = mathCos(end),
				sinEnd = mathSin(end),
				longArc = options.end - start &lt; mathPI ? 0 : 1;

			return [
				M,
				x + radius * cosStart,
				y + radius * sinStart,
				'A', // arcTo
				radius, // x radius
				radius, // y radius
				0, // slanting
				longArc, // long or short arc
				1, // clockwise
				x + radius * cosEnd,
				y + radius * sinEnd,
				L,
				x + innerRadius * cosEnd,
				y + innerRadius * sinEnd,
				'A', // arcTo
				innerRadius, // x radius
				innerRadius, // y radius
				0, // slanting
				longArc, // long or short arc
				0, // clockwise
				x + innerRadius * cosStart,
				y + innerRadius * sinStart,

				'Z' // close
			];
		}
	},

	/**
	 * Define a clipping rectangle
	 * @param {String} id
	 * @param {Number} x
	 * @param {Number} y
	 * @param {Number} width
	 * @param {Number} height
	 */
	clipRect: function (x, y, width, height) {
		var wrapper,
			id = PREFIX + idCounter++,

			clipPath = this.createElement('clipPath').attr({
				id: id
			}).add(this.defs);

		wrapper = this.rect(x, y, width, height, 0).add(clipPath);
		wrapper.id = id;

		return wrapper;
	},


	/**
	 * Take a color and return it if it's a string, make it a gradient if it's a
	 * gradient configuration object
	 *
	 * @param {Object} color The color or config object
	 */
	color: function(color, elem, prop) {
		var colorObject,
			regexRgba = /^rgba/;
		if (color &amp;&amp; color.linearGradient) {
			var renderer = this,
				strLinearGradient = 'linearGradient',
				linearGradient = color[strLinearGradient],
				id = PREFIX + idCounter++,
				gradientObject,
				stopColor,
				stopOpacity;
			gradientObject = renderer.createElement(strLinearGradient).attr({
				id: id,
				gradientUnits: 'userSpaceOnUse',
				x1: linearGradient[0],
				y1: linearGradient[1],
				x2: linearGradient[2],
				y2: linearGradient[3]
			}).add(renderer.defs);

			each(color.stops, function(stop) {
				if (regexRgba.test(stop[1])) {
					colorObject = Color(stop[1]);
					stopColor = colorObject.get('rgb');
					stopOpacity = colorObject.get('a');
				} else {
					stopColor = stop[1];
					stopOpacity = 1;
				}
				renderer.createElement('stop').attr({
					offset: stop[0],
					'stop-color': stopColor,
					'stop-opacity': stopOpacity
				}).add(gradientObject);
			});

			return 'url('+ this.url +'#'+ id +')';

		// Webkit and Batik can't show rgba.
		} else if (regexRgba.test(color)) {
			colorObject = Color(color);
			attr(elem, prop +'-opacity', colorObject.get('a'));

			return colorObject.get('rgb');


		} else {
			return color;
		}

	},


	/**
	 * Add text to the SVG object
	 * @param {String} str
	 * @param {Number} x Left position
	 * @param {Number} y Top position
	 */
	text: function(str, x, y) {

		// declare variables
		var renderer = this,
			defaultChartStyle = defaultOptions.chart.style,
			wrapper,
			box;

		x = mathRound(pick(x, 0));
		y = mathRound(pick(y, 0));

		wrapper = renderer.createElement('text')
			.attr({
				x: x,
				y: y,
				text: str
			})
			.css({
				fontFamily: defaultChartStyle.fontFamily,
				fontSize: defaultChartStyle.fontSize
			});

		wrapper.x = x;
		wrapper.y = y;
		return wrapper;
	},

	/**
	 * Add a label, a text item that can hold a colored or gradient background
	 * as well as a border and shadow.
	 * @param {string} str
	 * @param {Number} x
	 * @param {Number} y
	 * @param {String} shape
	 * @param {Number} anchorX In case the shape has a pointer, like a flag, this is the
	 *    coordinates it should be pinned to
	 * @param {Number} anchorY
	 */
	label: function(str, x, y, shape, anchorX, anchorY) {

		var renderer = this,
			wrapper = renderer.text(str),
			box,
			bBox,
			align = 'left',
			padding = 2,
			width,
			height,
			xAdjust,
			deferredAttr = {};

		function updateBoxSize() {
			bBox = (width === undefined || height === undefined || wrapper.styles.textAlign) &amp;&amp; wrapper.getBBox(true);
			var w = (width || bBox.width) + 2 * padding,
				h = (height || bBox.height) + 2 * padding,
				anchors;

			xAdjust = mathRound(w * { left: 0, center: 0.5, right: 1 }[align]);
			anchors = anchorX !== undefined &amp;&amp; {
				anchorX: anchorX - x + xAdjust,
				anchorY: anchorY - y
			};

			if (!box) {
				wrapper.box = box = shape ?
					renderer.symbol(shape, 0, 0, w, h, anchors) :
					renderer.rect(0, 0, w, h, 0, deferredAttr['stroke-width']);
				box.add(); // to get the translation right in IE
			}

			box.attr(merge({
				width: w,
				height: h
			}, anchors, deferredAttr));
			deferredAttr = null;
		}

		/**
		 * Set box attributes, or defer them if the box is not yet created
		 * @param {Object} key
		 * @param {Object} value
		 */
		function boxAttr(key, value) {
			if (box) {
				box.attr(key, value);
			} else {
				deferredAttr[key] = value;
			}

		}

		addEvent(wrapper, 'add', function() {


			updateBoxSize();

			var boxElem = box.element,
				wrapperElem = wrapper.element,
				zIndex = attr(wrapperElem, 'zIndex');
			if (defined(zIndex)) {
				attr(boxElem, 'zIndex', zIndex);
			}
			wrapperElem.parentNode.insertBefore(boxElem, wrapperElem);


			wrapper.attr({
				x: x,
				y: y
			});
		});

		addEvent(wrapper, 'setAttr', function(e) {
			var key = e.key,
				value = e.value,
				elem = wrapper,
				textAlign,
				ret;

			// change local variables
			if (key === 'width') {
				width = value;
				ret = false;
			} else if (key === 'height') {
				height = value;
				ret = false;
			} else if (key === 'padding') {
				padding = value;
				ret = false;

			// change local variable and set attribue as well
			} else if (key === 'align') {
				align = value;
				//ret = false;

			// apply these to the box and the text alike
			} else if (key === 'visibility' || key === 'zIndex') {
				boxAttr(key, value);
			}

			// apply these to the box but not to the text
			else if (key === 'stroke' || key === 'stroke-width' || key === 'fill' || key === 'r') {
				boxAttr(key, value);
				ret = false;
			}

			// change box attributes and return modified values
			else if (key === 'x') {
				textAlign = wrapper.styles.textAlign;
				boxAttr('translateX', value - xAdjust);
				if (align === 'left' &amp;&amp; defined(width) &amp;&amp; (textAlign === 'center' || textAlign === 'right')) {
					value += { center: 0.5, right: 1 }[textAlign] * (width - bBox.width);
				}
				ret = mathRound(value + { left: 1, center: 0, right: -1 }[align] * padding);
			} else if (key === 'y') {
				boxAttr('translateY', value);
				ret = mathRound(value + pInt(wrapper.styles.fontSize || 12) * 1.2);
			}

			else if (key === 'text') {
				e.callback = updateBoxSize;
			}
			if (ret !== UNDEFINED) {
				return ret;
			}
		});

		wrapper.txtToFront = wrapper.toFront;

		return extend(wrapper, {
			shadow: function(b) {
				box.shadow(b);
				return wrapper;
			},
			toFront: function() {
				box.toFront();
				wrapper.txtToFront();
			}
		});
	}
}; // end SVGRenderer


// general renderer
Renderer = SVGRenderer;



/* ****************************************************************************
 *                                                                            *
 * START OF INTERNET EXPLORER &lt;= 8 SPECIFIC CODE                              *
 *                                                                            *
 * For applications and websites that don't need IE support, like platform    *
 * targeted mobile apps and web apps, this code can be removed.               *
 *                                                                            *
 *****************************************************************************/

/**
 * @constructor
 */
var VMLRenderer;
if (!hasSVG) {

/**
 * The VML element wrapper.
 */
var VMLElement = extendClass( SVGElement, {

	/**
	 * Initialize a new VML element wrapper. It builds the markup as a string
	 * to minimize DOM traffic.
	 * @param {Object} renderer
	 * @param {Object} nodeName
	 */
	init: function(renderer, nodeName) {
		var markup =  ['&lt;', nodeName, ' filled="f" stroked="f"'],
			style = ['position: ', ABSOLUTE, ';'];

		// divs and shapes need size
		if (nodeName === 'shape' || nodeName === DIV) {
			style.push('left:0;top:0;width:10px;height:10px;');
		}
		if (docMode8) {
			style.push('visibility: ', nodeName === DIV ? HIDDEN : VISIBLE);
		}

		markup.push(' style="', style.join(''), '"/&gt;');

		// create element with default attributes and style
		if (nodeName) {
			markup = nodeName === DIV || nodeName === 'span' || nodeName === 'img' ?
				markup.join('')
				: renderer.prepVML(markup);
			this.element = createElement(markup);
		}

		this.renderer = renderer;
	},

	/**
	 * Add the node to the given parent
	 * @param {Object} parent
	 */
	add: function(parent) {
		var wrapper = this,
			renderer = wrapper.renderer,
			element = wrapper.element,
			box = renderer.box,
			inverted = parent &amp;&amp; parent.inverted,

			// get the parent node
			parentNode = parent ?
				parent.element || parent :
				box;


		// if the parent group is inverted, apply inversion on all children
		if (inverted) { // only on groups
			renderer.invertChild(element, parentNode);
		}

		// issue #140 workaround - related to #61 and #74
		if (docMode8 &amp;&amp; parentNode.gVis === HIDDEN) {
			css(element, { visibility: HIDDEN });
		}

		// append it
		parentNode.appendChild(element);

		// align text after adding to be able to read offset
		wrapper.added = true;
		if (wrapper.alignOnAdd &amp;&amp; !wrapper.deferUpdateTransform) {
			wrapper.updateTransform();
		}

		// fire an event for internal hooks
		fireEvent(wrapper, 'add');

		return wrapper;
	},

	/**
	 * Get or set attributes
	 */
	attr: function(hash, val) {
		var wrapper = this,
			key,
			value,
			i,
			result,
			element = wrapper.element || {},
			elemStyle = element.style,
			nodeName = element.nodeName,
			renderer = wrapper.renderer,
			symbolName = wrapper.symbolName,
			childNodes,
			hasSetSymbolSize,
			shadows = wrapper.shadows,
			skipAttr,
			ret = wrapper;

		// single key-value pair
		if (isString(hash) &amp;&amp; defined(val)) {
			key = hash;
			hash = {};
			hash[key] = val;
		}

		// used as a getter, val is undefined
		if (isString(hash)) {
			key = hash;
			if (key === 'strokeWidth' || key === 'stroke-width') {
				ret = wrapper.strokeweight;
			} else {
				ret = wrapper[key];
			}

		// setter
		} else {
			for (key in hash) {
				value = hash[key];
				skipAttr = false;

				fireEvent(wrapper, 'setAttr', { key: key, value: value }, function(e) {
					result = e.result;
					if (defined(result) &amp;&amp; result !== false) {
						value = result;
					}

					// prepare paths
					// symbols
					if (symbolName &amp;&amp; /^(x|y|r|start|end|width|height|innerR)/.test(key)) {
						// if one of the symbol size affecting parameters are changed,
						// check all the others only once for each call to an element's
						// .attr() method
						if (!hasSetSymbolSize) {

							wrapper.symbolAttr(hash);

							hasSetSymbolSize = true;
						}

						skipAttr = true;

					} else if (key === 'd') {
						value = value || [];
						wrapper.d = value.join(' '); // used in getter for animation

						// convert paths
						i = value.length;
						var convertedPath = [];
						while (i--) {

							// Multiply by 10 to allow subpixel precision.
							// Substracting half a pixel seems to make the coordinates
							// align with SVG, but this hasn't been tested thoroughly
							if (isNumber(value[i])) {
								convertedPath[i] = mathRound(value[i] * 10) - 5;
							}
							// close the path
							else if (value[i] === 'Z') {
								convertedPath[i] = 'x';
							}
							else {
								convertedPath[i] = value[i];
							}

						}
						value = convertedPath.join(' ') || 'x';
						element.path = value;

						// update shadows
						if (shadows) {
							i = shadows.length;
							while (i--) {
								shadows[i].path = value;
							}
						}
						skipAttr = true;

					// directly mapped to css
					} else if (key === 'zIndex' || key === 'visibility') {

						// issue 61 workaround
						if (docMode8 &amp;&amp; key === 'visibility' &amp;&amp; nodeName === 'DIV') {
							element.gVis = value;
							childNodes = element.childNodes;
							i = childNodes.length;
							while (i--) {
								css(childNodes[i], { visibility: value });
							}
							if (value === VISIBLE) { // issue 74
								value = null;
							}
						}

						if (value) {
							elemStyle[key] = value;
						}



						skipAttr = true;

					// width and height
					} else if (key === 'width' || key === 'height') {


						// clipping rectangle special
						if (wrapper.updateClipping) {
							wrapper[key] = value;
							wrapper.updateClipping();

						} else {
							// normal
							elemStyle[key] = value;
						}

						skipAttr = true;

					// x and y
					} else if (/^(x|y)$/.test(key)) {

						wrapper[key] = value; // used in getter

						if (element.tagName === 'SPAN') {
							wrapper.updateTransform();

						} else {
							elemStyle[{ x: 'left', y: 'top' }[key]] = value;
						}

					// class name
					} else if (key === 'class') {
						// IE8 Standards mode has problems retrieving the className
						element.className = value;

					// stroke
					} else if (key === 'stroke') {

						value = renderer.color(value, element, key);

						key = 'strokecolor';

					// stroke width
					} else if (key === 'stroke-width' || key === 'strokeWidth') {
						element.stroked = value ? true : false;
						key = 'strokeweight';
						wrapper[key] = value; // used in getter, issue #113
						if (isNumber(value)) {
							value += PX;
						}

					// dashStyle
					} else if (key === 'dashstyle') {
						var strokeElem = element.getElementsByTagName('stroke')[0] ||
							createElement(renderer.prepVML(['<stroke>']), null, null, element);
						strokeElem[key] = value || 'solid';
						wrapper.dashstyle = value; /* because changing stroke-width will change the dash length
							and cause an epileptic effect */
						skipAttr = true;

					// fill
					} else if (key === 'fill') {

						if (nodeName === 'SPAN') { // text color
							elemStyle.color = value;
						} else {
							element.filled = value !== NONE ? true : false;

							value = renderer.color(value, element, key);

							key = 'fillcolor';
						}

					// translation for animation
					} else if (key === 'translateX' || key === 'translateY' || key === 'rotation' || key === 'align') {
						if (key === 'align') {
							key = 'textAlign';
						}
						wrapper[key] = value;
						wrapper.updateTransform();

						skipAttr = true;
					}

					// text for rotated and non-rotated elements
					else if (key === 'text') {
						this.bBox = null;
						element.innerHTML = value;
						skipAttr = true;
					}


					// let the shadow follow the main element
					if (shadows &amp;&amp; key === 'visibility') {
						i = shadows.length;
						while (i--) {
							shadows[i].style[key] = value;
						}
					}



					if (!skipAttr) {
						if (docMode8) { // IE8 setAttribute bug
							try {
							element[key] = value;
							} catch(e) {
								console.log([element.tagName, key, value].join(', '));
								}
						} else {
							attr(element, key, value);
						}
					}


					if (e.callback) {
						e.callback();
					}
				});
			}
		}
		return ret;
	},

	/**
	 * Set the element's clipping to a predefined rectangle
	 *
	 * @param {String} id The id of the clip rectangle
	 */
	clip: function(clipRect) {
		var wrapper = this,
			clipMembers = clipRect.members;

		clipMembers.push(wrapper);
		wrapper.destroyClip = function() {
			erase(clipMembers, wrapper);
		};
		return wrapper.css(clipRect.getCSS(wrapper.inverted));
	},

	/**
	 * Set styles for the element
	 * @param {Object} styles
	 */
	css: function(styles) {
		var wrapper = this,
			element = wrapper.element,
			textWidth = styles &amp;&amp; element.tagName === 'SPAN' &amp;&amp; styles.width;

		if (textWidth) {
			delete styles.width;
			wrapper.textWidth = textWidth;
			wrapper.updateTransform();
		}

		wrapper.styles = extend(wrapper.styles, styles);
		css(wrapper.element, styles);

		return wrapper;
	},

	/**
	 * Extend element.destroy by removing it from the clip members array
	 */
	destroy: function() {
		var wrapper = this;

		if (wrapper.destroyClip) {
			wrapper.destroyClip();
		}

		SVGElement.prototype.destroy.apply(wrapper);
	},

	/**
	 * Remove all child nodes of a group, except the v:group element
	 */
	empty: function() {
		var element = this.element,
			childNodes = element.childNodes,
			i = childNodes.length,
			node;

		while (i--) {
			node = childNodes[i];
			node.parentNode.removeChild(node);
		}
	},

	/**
	 * VML override for calculating the bounding box based on offsets
	 * @param {Boolean} refresh Whether to force a fresh value from the DOM or to 
	 * use the cached value
	 * 
	 * @return {Object} A hash containing values for x, y, width and height
	 */

	getBBox: function(refresh) {
		var wrapper = this,
			element = wrapper.element,
			bBox = wrapper.bBox;

		// faking getBBox in exported SVG in legacy IE
		if (!bBox || refresh) {
			// faking getBBox in exported SVG in legacy IE
			if (element.nodeName === 'text') {
				element.style.position = ABSOLUTE;
			}

			bBox = wrapper.bBox = {
				x: element.offsetLeft,
				y: element.offsetTop,
				width: element.offsetWidth,
				height: element.offsetHeight
			};
		}

		return bBox;
	},

	/**
	 * Add an event listener. VML override for normalizing event parameters.
	 * @param {String} eventType
	 * @param {Function} handler
	 */
	on: function(eventType, handler) {
		// simplest possible event model for internal use
		this.element['on'+ eventType] = function() {
			var evt = win.event;
			evt.target = evt.srcElement;
			handler(evt);
		};
		return this;
	},


	/**
	 * VML override private method to update elements based on internal
	 * properties based on SVG transform
	 */
	updateTransform: function(hash) {
		// aligning non added elements is expensive
		if (!this.added) {
			this.alignOnAdd = true;
			return;
		}

		var wrapper = this,
			elem = wrapper.element,
			translateX = wrapper.translateX || 0,
			translateY = wrapper.translateY || 0,
			x = wrapper.x || 0,
			y = wrapper.y || 0,
			align = wrapper.textAlign || 'left',
			alignCorrection = { left: 0, center: 0.5, right: 1 }[align],
			nonLeft = align &amp;&amp; align !== 'left',
			shadows = wrapper.shadows;

		// apply translate
		if (translateX || translateY) {
			css(elem, {
				marginLeft: translateX,
				marginTop: translateY
			});
			if (shadows) { // used in labels/tooltip
				each(shadows, function(shadow) {
					css(shadow, {
						marginLeft: translateX + 1,
						marginTop: translateY + 1
					});
				});
			}
		}

		// apply inversion
		if (wrapper.inverted) { // wrapper is a group
			each(elem.childNodes, function(child) {
				wrapper.renderer.invertChild(child, elem);
			});
		}

		if (elem.tagName === 'SPAN') {

			var width, height,
				rotation = wrapper.rotation,
				lineHeight,
				radians = 0,
				costheta = 1,
				sintheta = 0,
				quad,
				textWidth = pInt(wrapper.textWidth),
				xCorr = wrapper.xCorr || 0,
				yCorr = wrapper.yCorr || 0,
				currentTextTransform = [rotation, align, elem.innerHTML, wrapper.textWidth].join(',');

			if (currentTextTransform !== wrapper.cTT) { // do the calculations and DOM access only if properties changed

				if (defined(rotation)) {
					radians = rotation * deg2rad; // deg to rad
					costheta = mathCos(radians);
					sintheta = mathSin(radians);

					// Adjust for alignment and rotation.
					// Test case: http://highcharts.com/tests/?file=text-rotation
					css(elem, {
						filter: rotation ? ['progid:DXImageTransform.Microsoft.Matrix(M11=', costheta,
							', M12=', -sintheta, ', M21=', sintheta, ', M22=', costheta,
							', sizingMethod=\'auto expand\')'].join('') : NONE
					});
				}

				width = pick(wrapper.elemWidth, elem.offsetWidth);
				height = pick(wrapper.elemHeight, elem.offsetHeight);

				// update textWidth
				if (width &gt; textWidth) {
					css(elem, {
						width: textWidth +PX,
						display: 'block',
						whiteSpace: 'normal'
					});
					width = textWidth;
				}

				// correct x and y
				lineHeight = mathRound((pInt(elem.style.fontSize) || 12) * 1.2);
				xCorr = costheta &lt; 0 &amp;&amp; -width;
				yCorr = sintheta &lt; 0 &amp;&amp; -height;

				// correct for lineHeight and corners spilling out after rotation
				quad = costheta * sintheta &lt; 0;
				xCorr += sintheta * lineHeight * (quad ? 1 - alignCorrection : alignCorrection);
				yCorr -= costheta * lineHeight * (rotation ? (quad ? alignCorrection : 1 - alignCorrection) : 1);

				// correct for the length/height of the text
				if (nonLeft) {
					xCorr -= width * alignCorrection * (costheta &lt; 0 ? -1 : 1);
					if (rotation) {
						yCorr -= height * alignCorrection * (sintheta &lt; 0 ? -1 : 1);
					}
					css(elem, {
						textAlign: align
					});
				}

				// record correction
				wrapper.xCorr = xCorr;
				wrapper.yCorr = yCorr;
			}

			// apply position with correction
			css(elem, {
				left: x + xCorr,
				top: y + yCorr
			});

			// record current text transform
			wrapper.cTT = currentTextTransform;
		}
	},

	/**
	 * Apply a drop shadow by copying elements and giving them different strokes
	 * @param {Boolean} apply
	 */
	shadow: function(apply, group) {
		var shadows = [],
			i,
			element = this.element,
			renderer = this.renderer,
			shadow,
			elemStyle = element.style,
			markup,
			path = element.path;

		// some times empty paths are not strings
		if (path &amp;&amp; typeof path.value !== 'string') {
			path = 'x';
		}

		if (apply) {
			for (i = 1; i &lt;= 3; i++) {
				markup = ['<shape isshadow="true" strokeweight="', ( 7 - 2 * i ) ,
					'" filled="false" path="', path,
					'" coordsize="100,100" style="">'];
				shadow = createElement(renderer.prepVML(markup),
					null, {
						left: pInt(elemStyle.left) + 1,
						top: pInt(elemStyle.top) + 1
					}
				);

				// apply the opacity
				markup = ['<stroke color="black" opacity="', (0.05 * i), '">'];
				createElement(renderer.prepVML(markup), null, null, shadow);


				// insert it
				if (group) {
					group.element.appendChild(shadow);
				} else {
					element.parentNode.insertBefore(shadow, element);
				}

				// record it
				shadows.push(shadow);

			}

			this.shadows = shadows;
		}
		return this;

	}
});

/**
 * The VML renderer
 */
VMLRenderer = function() {
	this.init.apply(this, arguments);
};
VMLRenderer.prototype = merge( SVGRenderer.prototype, { // inherit SVGRenderer

	Element: VMLElement,
	isIE8: userAgent.indexOf('MSIE 8.0') &gt; -1,


	/**
	 * Initialize the VMLRenderer
	 * @param {Object} container
	 * @param {Number} width
	 * @param {Number} height
	 */
	init: function(container, width, height) {
		var renderer = this,
			boxWrapper;

		renderer.alignedObjects = [];

		boxWrapper = renderer.createElement(DIV);
		container.appendChild(boxWrapper.element);


		// generate the containing box
		renderer.box = boxWrapper.element;
		renderer.boxWrapper = boxWrapper;


		renderer.setSize(width, height, false);

		// The only way to make IE6 and IE7 print is to use a global namespace. However,
		// with IE8 the only way to make the dynamic shapes visible in screen and print mode
		// seems to be to add the xmlns attribute and the behaviour style inline.
		if (!doc.namespaces.hcv) {

			doc.namespaces.add('hcv', 'urn:schemas-microsoft-com:vml');

			// setup default css
			doc.createStyleSheet().cssText =
				'hcv\\:fill, hcv\\:path, hcv\\:shape, hcv\\:stroke'+
				'{ behavior:url(#default#VML); display: inline-block; } ';

		}
	},

	/**
	 * Define a clipping rectangle. In VML it is accomplished by storing the values
	 * for setting the CSS style to all associated members.
	 *
	 * @param {Number} x
	 * @param {Number} y
	 * @param {Number} width
	 * @param {Number} height
	 */
	clipRect: function (x, y, width, height) {

		// create a dummy element
		var clipRect = this.createElement();

		// mimic a rectangle with its style object for automatic updating in attr
		return extend(clipRect, {
			members: [],
			left: x,
			top: y,
			width: width,
			height: height,
			getCSS: function(inverted) {
				var rect = this,//clipRect.element.style,
					top = rect.top,
					left = rect.left,
					right = left + rect.width,
					bottom = top + rect.height,
					ret = {
						clip: 'rect('+
							mathRound(inverted ? left : top) + 'px,'+
							mathRound(inverted ? bottom : right) + 'px,'+
							mathRound(inverted ? right : bottom) + 'px,'+
							mathRound(inverted ? top : left) +'px)'
					};

				// issue 74 workaround
				if (!inverted &amp;&amp; docMode8) {
					extend(ret, {
						width: right +PX,
						height: bottom +PX
					});
				}
				return ret;
			},

			// used in attr and animation to update the clipping of all members
			updateClipping: function() {
				each(clipRect.members, function(member) {
					member.css(clipRect.getCSS(member.inverted));
				});
			}
		});

	},


	/**
	 * Take a color and return it if it's a string, make it a gradient if it's a
	 * gradient configuration object, and apply opacity.
	 *
	 * @param {Object} color The color or config object
	 */
	color: function(color, elem, prop) {
		var colorObject,
			regexRgba = /^rgba/,
			markup;

		if (color &amp;&amp; color.linearGradient) {

			var stopColor,
				stopOpacity,
				linearGradient = color.linearGradient,
				angle,
				color1,
				opacity1,
				color2,
				opacity2;

			each(color.stops, function(stop, i) {
				if (regexRgba.test(stop[1])) {
					colorObject = Color(stop[1]);
					stopColor = colorObject.get('rgb');
					stopOpacity = colorObject.get('a');
				} else {
					stopColor = stop[1];
					stopOpacity = 1;
				}

				if (!i) { // first
					color1 = stopColor;
					opacity1 = stopOpacity;
				} else {
					color2 = stopColor;
					opacity2 = stopOpacity;
				}
			});



			// calculate the angle based on the linear vector
			angle = 90  - math.atan(
				(linearGradient[3] - linearGradient[1]) / // y vector
				(linearGradient[2] - linearGradient[0]) // x vector
				) * 180 / mathPI;

			// when colors attribute is used, the meanings of opacity and o:opacity2
			// are reversed.
			markup = ['&lt;', prop, ' colors="0% ', color1, ',100% ', color2, '" angle="', angle,
				'" opacity="', opacity2, '" o:opacity2="', opacity1,
				'" type="gradient" focus="100%" /&gt;'];
			createElement(this.prepVML(markup), null, null, elem);



		// if the color is an rgba color, split it and add a fill node
		// to hold the opacity component
		} else if (regexRgba.test(color) &amp;&amp; elem.tagName !== 'IMG') {

			colorObject = Color(color);

			markup = ['&lt;', prop, ' opacity="', colorObject.get('a'), '"/&gt;'];
			createElement(this.prepVML(markup), null, null, elem);

			return colorObject.get('rgb');


		} else {
			return color;
		}

	},

	/**
	 * Take a VML string and prepare it for either IE8 or IE6/IE7.
	 * @param {Array} markup A string array of the VML markup to prepare
	 */
	prepVML: function(markup) {
		var vmlStyle = 'display:inline-block;behavior:url(#default#VML);',
			isIE8 = this.isIE8;

		markup = markup.join('');

		if (isIE8) { // add xmlns and style inline
			markup = markup.replace('/&gt;', ' xmlns="urn:schemas-microsoft-com:vml" /&gt;');
			if (markup.indexOf('style="') === -1) {
				markup = markup.replace('/&gt;', ' style="'+ vmlStyle +'" /&gt;');
			} else {
				markup = markup.replace('style="', 'style="'+ vmlStyle);
			}

		} else { // add namespace
			markup = markup.replace('&lt;', '<hcv:'); markup;="" rotated="" aligned="" text="" str="" function(str,="" y)="" defaultchartstyle="defaultOptions.chart.style;" span="" )="" text:="" str,="" mathround(x),="" mathround(y)="" })="" .css({="" whitespace:="" nowrap="" fontfamily:="" defaultchartstyle.fontfamily,="" fontsize:="" defaultchartstyle.fontsize="" element="" {array}="" path:="" function="" (path)="" this.createelement(="" shape="" subpixel="" precision="" down="" 0.1="" (width="" coordsize:="" 100="" ,="" d:="" path="" element.="" in="" circles="" are="" implemented="" as="" shapes,="" which="" is="" faster="" than="" v:oval="" r="" circle:="" function(x,="" r)="" this.symbol(="" circle="" ).attr({="" x:="" y:="" r:="" r});="" using="" outer="" an="" inner="" allow="" rotating="" flipping.="" simple="" v:group="" would="" have="" problems="" with="" positioning="" child="" elements="" css="" clip.="" of="" group="" g:="" function(name)="" wrapper,="" attribs;="" set="" (name)="" attribs="{" classname="" name,="" class="" :="" prefix="" +="" name="" };="" }="" the="" div="" hold="" and="" clipping="" wrapper="this.createElement(DIV).attr(attribs);" return="" wrapper;="" },="" **="" vml="" override="" to="" create="" a="" regular="" html="" image="" {string}="" x="" y="" width="" @param="" {number}="" height="10px)" *="" image:="" function(src,="" x,="" y,="" width,="" height)="" {="" var="" obj="this.createElement('img')" .attr({="" src:="" src="" });="" if="" (arguments.length=""> 1) {
			obj.css({
				left: x,
				top: y,
				width: width,
				height: height
			});
		}
		return obj;
	},

	/**
	 * VML uses a shape for rect to overcome bugs and rotation problems
	 */
	rect: function(x, y, width, height, r, strokeWidth) {

		if (isObject(x)) {
			y = x.y;
			width = x.width;
			height = x.height;
			strokeWidth = x.strokeWidth;
			x = x.x;
		}
		var wrapper = this.symbol('rect');
		wrapper.r = r;

		return wrapper.attr(wrapper.crisp(strokeWidth, x, y, mathMax(width, 0), mathMax(height, 0)));
	},

	/**
	 * In the VML renderer, each child of an inverted div (group) is inverted
	 * @param {Object} element
	 * @param {Object} parentNode
	 */
	invertChild: function(element, parentNode) {
		var parentStyle = parentNode.style;

		css(element, {
			flip: 'x',
			left: pInt(parentStyle.width) - 10,
			top: pInt(parentStyle.height) - 10,
			rotation: -90
		});
	},

	/**
	 * Symbol definitions that override the parent SVG renderer's symbols
	 *
	 */
	symbols: {
		// VML specific arc function
		arc: function (x, y, w, h, options) {
			var start = options.start,
				end = options.end,
				radius = w,
				cosStart = mathCos(start),
				sinStart = mathSin(start),
				cosEnd = mathCos(end),
				sinEnd = mathSin(end),
				innerRadius = options.innerR,
				circleCorrection = 0.07 / radius,
				innerCorrection = (innerRadius &amp;&amp; 0.1 / innerRadius) || 0;

			if (end - start === 0) { // no angle, don't show it.
				return ['x'];

			//} else if (end - start == 2 * mathPI) { // full circle
			} else if (2 * mathPI - end + start &lt; circleCorrection) { // full circle
				// empirical correction found by trying out the limits for different radii
				cosEnd = - circleCorrection;
			} else if (end - start &lt; innerCorrection) { // issue #186, another mysterious VML arc problem
				cosEnd = mathCos(start + innerCorrection);
			}

			return [
				'wa', // clockwise arc to
				x - radius, // left
				y - radius, // top
				x + radius, // right
				y + radius, // bottom
				x + radius * cosStart, // start x
				y + radius * sinStart, // start y
				x + radius * cosEnd, // end x
				y + radius * sinEnd, // end y


				'at', // anti clockwise arc to
				x - innerRadius, // left
				y - innerRadius, // top
				x + innerRadius, // right
				y + innerRadius, // bottom
				x + innerRadius * cosEnd, // start x
				y + innerRadius * sinEnd, // start y
				x + innerRadius * cosStart, // end x
				y + innerRadius * sinStart, // end y

				'x', // finish path
				'e' // close
			];

		},
		// Add circle symbol path. This performs significantly faster than v:oval.
		circle: function (x, y, w, h) {

			return [
				'wa', // clockwisearcto
				x, // left
				y, // top
				x + w, // right
				y + h, // bottom
				x + w, // start x
				y + h / 2,     // start y
				x + w, // end x
				y + h / 2,     // end y
				//'x', // finish path
				'e' // close
			];
		},
		/**
		 * Add rectangle symbol path which eases rotation and omits arcsize problems
		 * compared to the built-in VML roundrect shape
		 *
		 * @param {Number} left Left position
		 * @param {Number} top Top position
		 * @param {Number} r Border radius
		 * @param {Object} options Width and height
		 */

		rect: function (left, top, width, height, options) {
			/*for (var n in r) {
				logTime &amp;&amp; console.log(n)
				}*/

			if (!defined(options)) {
				return [];
			}
			var right = left + width,
				bottom = top + height,
				r = mathMin(options.r || 0, width, height);

			return [
				M,
				left + r, top,

				L,
				right - r, top,
				'wa',
				right - 2 * r, top,
				right, top + 2 * r,
				right - r, top,
				right, top + r,

				L,
				right, bottom - r,
				'wa',
				right - 2 * r, bottom - 2 * r,
				right, bottom,
				right, bottom - r,
				right - r, bottom,

				L,
				left + r, bottom,
				'wa',
				left, bottom - 2 * r,
				left + 2 * r, bottom,
				left + r, bottom,
				left, bottom - r,

				L,
				left, top + r,
				'wa',
				left, top,
				left + 2 * r, top + 2 * r,
				left, top + r,
				left + r, top,


				'x',
				'e'
			];

		}
	}
});

	// general renderer
	Renderer = VMLRenderer;
}

/* ****************************************************************************
 *                                                                            *
 * END OF INTERNET EXPLORER &lt;= 8 SPECIFIC CODE                                *
 *                                                                            *
 *****************************************************************************/

/**
 * The chart class
 * @param {Object} options
 * @param {Function} callback Function to run when the chart has loaded
 */
function Chart (options, callback) {

	defaultXAxisOptions = merge(defaultXAxisOptions, defaultOptions.xAxis);
	defaultYAxisOptions = merge(defaultYAxisOptions, defaultOptions.yAxis);
	defaultOptions.xAxis = defaultOptions.yAxis = null;

	// Handle regular options
	var seriesOptions = options.series; // skip merging data points to increase performance
	options.series = null;
	options = merge(defaultOptions, options); // do the merge
	options.series = seriesOptions; // set back the series data

	// Define chart variables
	var optionsChart = options.chart,
		optionsMargin = optionsChart.margin,
		margin = isObject(optionsMargin) ?
			optionsMargin :
			[optionsMargin, optionsMargin, optionsMargin, optionsMargin],
		optionsMarginTop = pick(optionsChart.marginTop, margin[0]),
		optionsMarginRight = pick(optionsChart.marginRight, margin[1]),
		optionsMarginBottom = pick(optionsChart.marginBottom, margin[2]),
		optionsMarginLeft = pick(optionsChart.marginLeft, margin[3]),
		spacingTop = optionsChart.spacingTop,
		spacingRight = optionsChart.spacingRight,
		spacingBottom = optionsChart.spacingBottom,
		spacingLeft = optionsChart.spacingLeft,
		spacingBox,
		chartTitleOptions,
		chartSubtitleOptions,
		plotTop,
		marginRight,
		marginBottom,
		plotLeft,
		axisOffset,
		renderTo,
		renderToClone,
		container,
		containerId,
		containerWidth,
		containerHeight,
		chartWidth,
		chartHeight,
		oldChartWidth,
		oldChartHeight,
		chartBackground,
		plotBackground,
		plotBGImage,
		plotBorder,
		chart = this,
		chartEvents = optionsChart.events,
		runChartClick = chartEvents &amp;&amp; !!chartEvents.click,
		eventType,
		isInsidePlot, // function
		tooltip,
		mouseIsDown,
		loadingDiv,
		loadingSpan,
		loadingShown,
		plotHeight,
		plotWidth,
		tracker,
		trackerGroup,
		placeTrackerGroup,
		legend,
		legendWidth,
		legendHeight,
		chartPosition,// = getPosition(container),
		hasCartesianSeries = optionsChart.showAxes,
		isResizing = 0,
		axes = [],
		maxTicks, // handle the greatest amount of ticks on grouped axes
		series = [],
		inverted,
		renderer,
		tooltipTick,
		tooltipInterval,
		hoverX,
		drawChartBox, // function
		getMargins, // function
		resetMargins, // function
		setChartSize, // function
		resize,
		zoom, // function
		zoomOut; // function


	/**
	 * Create a new axis object
	 * @param {Object} options
	 */
	function Axis (options) {

		// Define variables
		var isXAxis = options.isX,
			opposite = options.opposite, // needed in setOptions
			horiz = inverted ? !isXAxis : isXAxis,
			side = horiz ?
				(opposite ? 0 /* top */  : 2 /* bottom */) :
				(opposite ? 1 /* right*/ : 3 /* left */  ),
			stacks = {};


		options = merge(
				isXAxis ? defaultXAxisOptions : defaultYAxisOptions,
				[defaultTopAxisOptions, defaultRightAxisOptions,
					defaultBottomAxisOptions, defaultLeftAxisOptions][side],
				options
			);

		var axis = this,
			type = options.type,
			isDatetimeAxis = type === 'datetime',
			isLog = type === 'logarithmic',
			offset = options.offset || 0,
			xOrY = isXAxis ? 'x' : 'y',
			axisLength,
			transA, // translation factor
			transB, // translation addend
			oldTransA, // used for prerendering
			axisLeft,
			axisTop,
			axisWidth,
			axisHeight,
			axisBottom,
			axisRight,
			translate, // fn
			getPlotLinePath, // fn
			axisGroup,
			gridGroup,
			axisLine,
			dataMin,
			dataMax,
			associatedSeries,
			range = options.range,
			userMin,
			userMax,
			max = null,
			min = null,
			oldMin,
			oldMax,
			minPadding = options.minPadding,
			maxPadding = options.maxPadding,
			isLinked = defined(options.linkedTo),
			ignoreMinPadding, // can be set to true by a column or bar series
			ignoreMaxPadding,
			usePercentage,
			events = options.events,
			eventType,
			padAxis,
			plotLinesAndBands = [],
			tickInterval,
			minorTickInterval,
			magnitude,
			tickPositions, // array containing predefined positions
			ticks = {},
			minorTicks = {},
			alternateBands = {},
			tickAmount,
			labelOffset,
			labelHeight,
			axisTitleMargin,// = options.title.margin,
			dateTimeLabelFormat,
			categories = options.categories,
			labelFormatter = options.labels.formatter ||  // can be overwritten by dynamic format
				function() {
					var value = this.value,
						ret;

					if (dateTimeLabelFormat) { // datetime axis
						ret = dateFormat(dateTimeLabelFormat, value);

					} else if (tickInterval % 1000000 === 0) { // use M abbreviation
						ret = (value / 1000000) +'M';

					} else if (tickInterval % 1000 === 0) { // use k abbreviation
						ret = (value / 1000) +'k';

					} else if (!categories &amp;&amp; value &gt;= 1000) { // add thousands separators
						ret = numberFormat(value, 0);

					} else { // strings (categories) and small numbers
						ret = value;
					}
					return ret;
				},

			staggerLines = horiz &amp;&amp; options.labels.staggerLines,
			reversed = options.reversed,
			tickmarkOffset = (categories &amp;&amp; options.tickmarkPlacement === 'between') ? 0.5 : 0;

		/**
		 * The Tick class
		 */
		function Tick(pos, minor) {
			var tick = this;
			tick.pos = pos;
			tick.minor = minor;
			tick.isNew = true;

			if (!minor) {
				tick.addLabel();
			}
		}
		Tick.prototype = {
			attachLabel: function() {
				var label = this.label;
				if (label &amp;&amp; !this.added) {
					label.deferUpdateTransform = true;
					label.add(axisGroup);
				}
			},
			updateTransformLabel: function() {
				var label = this.label;
				if (label) {
					label.deferUpdateTransform = false;
					label.updateTransform();
				}
			},
			computeBBox: function() {
				var label = this.label,
					bBox;
				if (label) {
					bBox = label.getBBox();
					label.elemWidth = bBox.width;
					label.elemHeight = bBox.height;
				}
			},
			/**
			 * Write the tick label
			 */
			addLabel: function() {
				var pos = this.pos,
					labelOptions = options.labels,
					str,
					withLabel = !((pos === min &amp;&amp; !pick(options.showFirstLabel, 1)) ||
						(pos === max &amp;&amp; !pick(options.showLastLabel, 0))),
					width = (categories &amp;&amp; horiz &amp;&amp; categories.length &amp;&amp;
						!labelOptions.step &amp;&amp; !labelOptions.staggerLines &amp;&amp;
						!labelOptions.rotation &amp;&amp;
						plotWidth / categories.length) ||
						(!horiz &amp;&amp; plotWidth / 2),
					css,
					label = this.label;


				// get the string
				str = labelFormatter.call({
						isFirst: pos === tickPositions[0],
						isLast: pos === tickPositions[tickPositions.length - 1],
						dateTimeLabelFormat: dateTimeLabelFormat,
						value: (categories &amp;&amp; categories[pos] ? categories[pos] : pos)
					});


				// prepare CSS
				css = width &amp;&amp; { width: mathMax(1, mathRound(width - 2 * (labelOptions.padding || 10))) +PX };
				css = extend(css, labelOptions.style);

				// first call
				if (!defined(label)) {
					this.label =
						defined(str) &amp;&amp; withLabel &amp;&amp; labelOptions.enabled ?
							renderer.text(
									str,
									0,
									0
								)
								.attr({
									align: labelOptions.align,
									rotation: labelOptions.rotation
								})
								// without position absolute, IE export sometimes is wrong
								.css(css):
							null;

				// update
				} else if (label) {
					label.attr({ text: str })
						.css(css);
				}
			},
			/**
			 * Get the offset height or width of the label
			 */
			getLabelSize: function() {
				var label = this.label;
				return label ?
					((this.labelBBox = label.getBBox()))[horiz ? 'height' : 'width'] :
					0;
				},
			/**
			 * Put everything in place
			 *
			 * @param index {Number}
			 * @param old {Boolean} Use old coordinates to prepare an animation into new position
			 */
			render: function(index, old) {
				var tick = this,
					major = !tick.minor,
					label = tick.label,
					pos = tick.pos,
					labelOptions = options.labels,
					gridLine = tick.gridLine,
					gridLineWidth = major ? options.gridLineWidth : options.minorGridLineWidth,
					gridLineColor = major ? options.gridLineColor : options.minorGridLineColor,
					dashStyle = major ?
						options.gridLineDashStyle :
						options.minorGridLineDashStyle,
					gridLinePath,
					mark = tick.mark,
					markPath,
					tickLength = major ? options.tickLength : options.minorTickLength,
					tickWidth = major ? options.tickWidth : (options.minorTickWidth || 0),
					tickColor = major ? options.tickColor : options.minorTickColor,
					tickPosition = major ? options.tickPosition : options.minorTickPosition,
					step = labelOptions.step,
					cHeight = (old &amp;&amp; oldChartHeight) || chartHeight,
					attribs,
					x,
					y;

				// get x and y position for ticks and labels
				x = horiz ?
					translate(pos + tickmarkOffset, null, null, old) + transB :
					axisLeft + offset + (opposite ? ((old &amp;&amp; oldChartWidth) || chartWidth) - axisRight - axisLeft : 0);
				
				y = horiz ?
					cHeight - axisBottom + offset - (opposite ? axisHeight : 0) :
					cHeight - translate(pos + tickmarkOffset, null, null, old) - transB;
				
				// create the grid line
				if (gridLineWidth) {
					gridLinePath = getPlotLinePath(pos + tickmarkOffset, gridLineWidth, old);

					if (gridLine === UNDEFINED) {
						attribs = {
							stroke: gridLineColor,
							'stroke-width': gridLineWidth
						};
						if (dashStyle) {
							attribs.dashstyle = dashStyle;
						}
						tick.gridLine = gridLine =
							gridLineWidth ?
								renderer.path(gridLinePath)
									.attr(attribs).add(gridGroup) :
								null;
					}
					if (gridLine &amp;&amp; gridLinePath) {
						gridLine.animate({
							d: gridLinePath
						});
					}
				}

				// create the tick mark
				if (tickWidth) {

					// negate the length
					if (tickPosition === 'inside') {
						tickLength = -tickLength;
					}
					if (opposite) {
						tickLength = -tickLength;
					}

					markPath = renderer.crispLine([
						M,
						x,
						y,
						L,
						x + (horiz ? 0 : -tickLength),
						y + (horiz ? tickLength : 0)
					], tickWidth);

					if (mark) { // updating
						mark.animate({
							d: markPath
						});
					} else { // first time
						tick.mark = renderer.path(
							markPath
						).attr({
							stroke: tickColor,
							'stroke-width': tickWidth
						}).add(axisGroup);
					}
				}

				// the label is created on init - now move it into place
				if (label &amp;&amp; !isNaN(x)) {
					x = x + labelOptions.x - (tickmarkOffset &amp;&amp; horiz ?
						tickmarkOffset * transA * (reversed ? -1 : 1) : 0);
					y = y + labelOptions.y - (tickmarkOffset &amp;&amp; !horiz ?
						tickmarkOffset * transA * (reversed ? 1 : -1) : 0);

					// vertically centered
					if (!defined(labelOptions.y)) {
						y += pInt(label.styles.lineHeight) * 0.9 - label.getBBox().height / 2;
					}


					// correct for staggered labels
					if (staggerLines) {
						y += (index / (step || 1) % staggerLines) * 16;
					}
					// apply step
					if (step) {
						// show those indices dividable by step
						label[index % step ? 'hide' : 'show']();
					}

					label[tick.isNew ? 'attr' : 'animate']({
						x: x,
						y: y
					});
				}

				tick.isNew = false;
			},
			/**
			 * Destructor for the tick prototype
			 */
			destroy: function() {
				var tick = this,
					n;
				for (n in tick) {
					if (tick[n] &amp;&amp; tick[n].destroy) {
						tick[n].destroy();
					}
				}
			}
		};

		/**
		 * The object wrapper for plot lines and plot bands
		 * @param {Object} options
		 */
		function PlotLineOrBand(options) {
			var plotLine = this;
			if (options) {
				plotLine.options = options;
				plotLine.id = options.id;
			}

			//plotLine.render()
			return plotLine;
		}

		PlotLineOrBand.prototype = {

		/**
		 * Render the plot line or plot band. If it is already existing,
		 * move it.
		 */
		render: function () {
			var plotLine = this,
				options = plotLine.options,
				optionsLabel = options.label,
				label = plotLine.label,
				width = options.width,
				to = options.to,
				toPath, // bands only
				from = options.from,
				dashStyle = options.dashStyle,
				svgElem = plotLine.svgElem,
				path = [],
				addEvent,
				eventType,
				xs,
				ys,
				x,
				y,
				color = options.color,
				zIndex = options.zIndex,
				events = options.events,
				attribs;

			// plot line
			if (width) {
				path = getPlotLinePath(options.value, width);
				attribs = {
					stroke: color,
					'stroke-width': width
				};
				if (dashStyle) {
					attribs.dashstyle = dashStyle;
				}
			}

			// plot band
			else if (defined(from) &amp;&amp; defined(to)) {
				// keep within plot area
				from = mathMax(from, min);
				to = mathMin(to, max);

				toPath = getPlotLinePath(to);
				path = getPlotLinePath(from);
				if (path &amp;&amp; toPath) {
					path.push(
						toPath[4],
						toPath[5],
						toPath[1],
						toPath[2]
					);
				} else { // outside the axis area
					path = null;
				}
				attribs = {
					fill: color
				};
			} else {
				return;
			}
			// zIndex
			if (defined(zIndex)) {
				attribs.zIndex = zIndex;
			}

			// common for lines and bands
			if (svgElem) {
				if (path) {
					svgElem.animate({
						d: path
					}, null, svgElem.onGetPath);
				} else {
					svgElem.hide();
					svgElem.onGetPath = function() {
						svgElem.show();
					};
				}
			} else if (path &amp;&amp; path.length) {
				plotLine.svgElem = svgElem = renderer.path(path)
					.attr(attribs).add();

				// events
				if (events) {
					addEvent = function(eventType) {
						svgElem.on(eventType, function(e) {
							events[eventType].apply(plotLine, [e]);
						});
					};
					for (eventType in events) {
						addEvent(eventType);
					}
				}
			}

			// the plot band/line label
			if (optionsLabel &amp;&amp; defined(optionsLabel.text) &amp;&amp; 
path &amp;&amp; path.length &amp;&amp; axisWidth &gt; 0 &amp;&amp; 
axisHeight &gt; 0) {
				// apply defaults
				optionsLabel = merge({
					align: horiz &amp;&amp; toPath &amp;&amp; 'center',
					x: horiz ? !toPath &amp;&amp; 4 : 10,
					verticalAlign : !horiz &amp;&amp; toPath &amp;&amp; 'middle',
					y: horiz ? toPath ? 16 : 10 : toPath ? 6 : -4,
					rotation: horiz &amp;&amp; !toPath &amp;&amp; 90
				}, optionsLabel);

				// add the SVG element
				if (!label) {
					plotLine.label = label = renderer.text(
							optionsLabel.text,
							0,
							0
						)
						.attr({
							align: optionsLabel.textAlign || optionsLabel.align,
							rotation: optionsLabel.rotation,
							zIndex: zIndex
						})
						.css(optionsLabel.style)
						.add();
				}

				// get the bounding box and align the label
				xs = [path[1], path[4], pick(path[6], path[1])];
				ys = [path[2], path[5], pick(path[7], path[2])];
				x = mathMin.apply(math, xs);
				y = mathMin.apply(math, ys);

				label.align(optionsLabel, false, {
					x: x,
					y: y,
					width: mathMax.apply(math, xs) - x,
					height: mathMax.apply(math, ys) - y
				});
				label.show();

			} else if (label) { // move out of sight
				label.hide();
			}

			// chainable
			return plotLine;
		},

		/**
		 * Remove the plot line or band
		 */
		destroy: function() {
			var obj = this,
				n;

			for (n in obj) {
				if (obj[n] &amp;&amp; obj[n].destroy) {
					obj[n].destroy(); // destroy SVG wrappers
				}
				delete obj[n];
			}
			// remove it from the lookup
			erase(plotLinesAndBands, obj);
		}
		};

		/**
		 * The class for stack items
		 */
		function StackItem(options, isNegative, x) {
			var stackItem = this;

			// Tells if the stack is negative
			stackItem.isNegative = isNegative;

			// Save the options to be able to style the label
			stackItem.options = options;

			// Save the x value to be able to position the label later
			stackItem.x = x;

			// The align options and text align varies on whether the stack is negative and
			// if the chart is inverted or not.
			// First test the user supplied value, then use the dynamic.
			stackItem.alignOptions = {
				align: options.align || (inverted ? (isNegative ? 'left' : 'right') : 'center'),
				verticalAlign: options.verticalAlign || (inverted ? 'middle' : (isNegative ? 'bottom' : 'top')),
				y: pick(options.y, inverted ? 4 : (isNegative ? 14 : -6)),
				x: pick(options.x, inverted ? (isNegative ? -6 : 6) : 0)
			};

			stackItem.textAlign = options.textAlign || (inverted ? (isNegative ? 'right' : 'left') : 'center');
		}

		StackItem.prototype = {
			/**
			 * Sets the total of this stack. Should be called when a serie is hidden or shown
			 * since that will affect the total of other stacks.
			 */
			setTotal: function(total) {
				this.total = total;
				this.cum = total;
			},

			/**
			 * Renders the stack total label and adds it to the stack label group.
			 */
			render: function(group) {
				var stackItem = this,									// aliased this
					str = stackItem.options.formatter.call(stackItem);  // format the text in the label

				// Change the text to reflect the new total and set visibility to hidden in case the serie is hidden
				if (stackItem.label) {
					stackItem.label.attr({text: str, visibility: HIDDEN});
				// Create new label
				} else {
					stackItem.label =
						chart.renderer.text(str, 0, 0)				// dummy positions, actual position updated with setOffset method in columnseries
							.css(stackItem.options.style)			// apply style
							.attr({align: stackItem.textAlign,			// fix the text-anchor
								rotation: stackItem.options.rotation,	// rotation
								visibility: HIDDEN })					// hidden until setOffset is called
							.add(group);							// add to the labels-group
				}
			},

			/**
			 * Sets the offset that the stack has from the x value and repositions the label.
			 */
			setOffset: function(xOffset, xWidth) {
				var stackItem = this,										// aliased this
					neg = stackItem.isNegative,								// special treatment is needed for negative stacks
					y = axis.translate(stackItem.total),					// stack value translated mapped to chart coordinates
					yZero = axis.translate(0),								// stack origin
					h = mathAbs(y - yZero),									// stack height
					x = chart.xAxis[0].translate(stackItem.x) + xOffset,	// stack x position
					plotHeight = chart.plotHeight,
					stackBox = {	// this is the box for the complete stack
							x: inverted ? (neg ? y : y - h) : x,
							y: inverted ? plotHeight - x - xWidth : (neg ? (plotHeight - y - h) : plotHeight - y),
							width: inverted ? h : xWidth,
							height: inverted ? xWidth : h
					};

				if (stackItem.label) {
					stackItem.label
						.align(stackItem.alignOptions, null, stackBox)	// align the label to the box
						.attr({visibility: VISIBLE});					// set visibility
				}
			}
		};

		/**
		 * Get the minimum and maximum for the series of each axis
		 */
		function getSeriesExtremes() {
			var posStack = [],
				negStack = [],
				i,
				run;

			// reset dataMin and dataMax in case we're redrawing
			dataMin = dataMax = null;

			// get an overview of what series are associated with this axis
			associatedSeries = [];
			each(series, function(serie) {
				var seriesOptions = serie.options;

				run = false;


				// match this axis against the series' given or implicated axis
				each(['xAxis', 'yAxis'], function(strAxis) {
					if (
						// the series is a cartesian type, and...
						serie.isCartesian &amp;&amp;
						// we're in the right x or y dimension, and...
						((strAxis === 'xAxis' &amp;&amp; isXAxis) || (strAxis === 'yAxis' &amp;&amp; !isXAxis)) &amp;&amp; (
							// the axis number is given in the options and matches this axis index, or
							(seriesOptions[strAxis] === options.index) ||
							// the axis index is not given
							(seriesOptions[strAxis] === UNDEFINED &amp;&amp; options.index === 0)
						)
					) {
						serie[strAxis] = axis;
						associatedSeries.push(serie);

						if (serie.options.padXAxis) {
							padAxis = true;
						}

						// the series is visible, run the min/max detection
						run = true;
					}
				});
				// ignore hidden series if opted
				if (!serie.visible &amp;&amp; optionsChart.ignoreHiddenSeries) {
					run = false;
				}

				if (run) {

					var stacking,
						posPointStack,
						negPointStack,
						stackKey,
						negKey;

					if (!isXAxis) {
						stacking = seriesOptions.stacking;
						usePercentage = stacking === 'percent';

						// create a stack for this particular series type
						if (stacking) {
							stackKey = serie.type + pick(seriesOptions.stack, '');
							negKey = '-'+ stackKey;
							serie.stackKey = stackKey; // used in translate

							posPointStack = posStack[stackKey] || []; // contains the total values for each x
							posStack[stackKey] = posPointStack;

							negPointStack = negStack[negKey] || [];
							negStack[negKey] = negPointStack;
						}
						if (usePercentage) {
							dataMin = 0;
							dataMax = 99;
						}
					}
					if (serie.isCartesian) { // line, column etc. need axes, pie doesn't

						var xData,
							yData,
							x,
							y,
							threshold = seriesOptions.threshold,
							yDataLength,
							activeYData = [],
							activeCounter = 0;

						if (isXAxis) {
							xData = serie.xData;
							dataMin = mathMin(pick(dataMin, xData[0]), mathMin.apply(math, xData));
							dataMax = mathMax(pick(dataMax, xData[0]), mathMax.apply(math, xData));
						} else {
							var isNegative,
								pointStack,
								key,
								j;

							// get clipped and grouped data
							serie.processData();

							var start = + new Date();

							xData = serie.processedXData;
							yData = serie.processedYData;
							yDataLength = yData.length;


							// loop over the non-null y values and read them into a local array
							for (i = 0; i &lt; yDataLength; i++) {
								y = yData[i];
								if (y !== null &amp;&amp; y !== UNDEFINED) {
									// read stacked values into a stack based on the x value,
									// the sign of y and the stack key
									if (stacking) {
										x = xData[i];
										isNegative = y &lt; 0;
										pointStack = isNegative ? negPointStack : posPointStack;
										key = isNegative ? negKey : stackKey;

										y = pointStack[x] =
											defined(pointStack[x]) ?
											pointStack[x] + y : y;


										// add the series
										if (!stacks[key]) {
											stacks[key] = {};
										}

										// If the StackItem is there, just update the values,
										// if not, create one first
										if (!stacks[key][x]) {
											stacks[key][x] = new StackItem(options.stackLabels, isNegative, x);
										}
										stacks[key][x].setTotal(y);

									}

									j = y.length;
									if (j) { // array, like ohlc data
										while (j--) {
											activeYData[activeCounter++] = y[j];
										}
									} else {
										activeYData[activeCounter++] = y;
									}
								}
							}
							if (!usePercentage) { // percentage stacks are always 0-100
								dataMin = mathMin(pick(dataMin, activeYData[0]), mathMin.apply(math, activeYData));
								dataMax = mathMax(pick(dataMax, activeYData[0]), mathMax.apply(math, activeYData));
							}


							// todo: instead of checking useThreshold, just set the threshold to 0
							// in area and column-like chart types
							if (serie.useThreshold &amp;&amp; threshold !== null) {
								if (dataMin &gt;= threshold) {
									dataMin = threshold;
									ignoreMinPadding = true;
								} else if (dataMax &lt; threshold) {
									dataMax = threshold;
									ignoreMaxPadding = true;
								}
							}


						}
					}
				}
			});

		}

		/**
		 * Translate from axis value to pixel position on the chart, or back
		 *
		 */
		translate = function(val, backwards, cvsCoord, old, handleLog) {
			var sign = 1,
				cvsOffset = 0,
				localA = old ? oldTransA : transA,
				localMin = old ? oldMin : min,
				returnValue;

			if (!localA) {
				localA = transA;
			}

			if (cvsCoord) {
				sign *= -1; // canvas coordinates inverts the value
				cvsOffset = axisLength;
			}
			if (reversed) { // reversed axis
				sign *= -1;
				cvsOffset -= sign * axisLength;
			}

			if (backwards) { // reverse translation
				if (reversed) {
					val = axisLength - val;
				}
				returnValue = val / localA + localMin; // from chart pixel to value
				if (isLog &amp;&amp; handleLog) {
					returnValue = lin2log(returnValue);
				}

			} else { // normal translation, from axis value to pixel, relative to plot
				if (isLog &amp;&amp; handleLog) {
					val = log2lin(val);
				}
				returnValue = sign * (val - localMin) * localA + cvsOffset;
			}

			return returnValue;
		};

		/**
		 * Create the path for a plot line that goes from the given value on
		 * this axis, across the plot to the opposite side
		 * @param {Number} value
		 * @param {Number} lineWidth Used for calculation crisp line
		 * @param {Number] old Use old coordinates (for resizing and rescaling)
		 */
		getPlotLinePath = function(value, lineWidth, old) {
			var x1,
				y1,
				x2,
				y2,
				translatedValue = translate(value, null, null, old),
				cHeight = (old &amp;&amp; oldChartHeight) || chartHeight,
				cWidth = (old &amp;&amp; oldChartWidth) || chartWidth,
				skip;

			x1 = x2 = mathRound(translatedValue + transB);
			y1 = y2 = mathRound(cHeight - translatedValue - transB);

			if (isNaN(translatedValue)) { // no min or max
				skip = true;

			} else if (horiz) {
				y1 = axisTop;
				y2 = cHeight - axisBottom;
				if (x1 &lt; axisLeft || x1 &gt; axisLeft + axisWidth) {
					skip = true;
				}
			} else {
				x1 = axisLeft;
				x2 = cWidth - axisRight;

				if (y1 &lt; axisTop || y1 &gt; axisTop + axisHeight) {
					skip = true;
				}
			}
			return skip ?
				null :
				renderer.crispLine([M, x1, y1, L, x2, y2], lineWidth || 0);
		};

		/**
		 * Fix JS round off float errors
		 * @param {Number} num
		 */
		function correctFloat(num) {
			var invMag, ret = num;
			magnitude = pick(magnitude, math.pow(10, mathFloor(math.log(tickInterval) / math.LN10)));

			if (magnitude &lt; 1) {
				invMag = mathRound(1 / magnitude)  * 10;
				ret = mathRound(num * invMag) / invMag;
			}
			return ret;
		}

		/**
		 * Set the tick positions of a linear axis to round values like whole tens or every five.
		 */
		function setLinearTickPositions() {

			var i,
				roundedMin = correctFloat(mathFloor(min / tickInterval) * tickInterval),
				roundedMax = correctFloat(mathCeil(max / tickInterval) * tickInterval);

			tickPositions = [];

			// populate the intermediate values
			i = correctFloat(roundedMin);
			while (i &lt;= roundedMax) {
				tickPositions.push(i);
				i = correctFloat(i + tickInterval);
			}

		}

		/**
		 * Set the tick positions to round values and optionally extend the extremes
		 * to the nearest tick
		 */
		function setTickPositions(secondPass) {
			var length,
				catPad,
				linkedParent,
				linkedParentExtremes,
				tickIntervalOption = options.tickInterval,
				tickPixelIntervalOption = options.tickPixelInterval,
				maxZoom = options.maxZoom || (
					isXAxis &amp;&amp; !defined(options.min) &amp;&amp; !defined(options.max) ?
						mathMin(chart.smallestInterval * 5, dataMax - dataMin) :
						null
				),
				zoomOffset;

			axisLength = horiz ? axisWidth : axisHeight;

			// linked axis gets the extremes from the parent axis
			if (isLinked) {
				linkedParent = chart[isXAxis ? 'xAxis' : 'yAxis'][options.linkedTo];
				linkedParentExtremes = linkedParent.getExtremes();
				min = pick(linkedParentExtremes.min, linkedParentExtremes.dataMin);
				max = pick(linkedParentExtremes.max, linkedParentExtremes.dataMax);
			}

			// initial min and max from the extreme data values
			else {
				min = pick(userMin, options.min, dataMin);
				max = pick(userMax, options.max, dataMax);
			}

			if (isLog) {
				min = log2lin(min);
				max = log2lin(max);
			}

			// handle zoomed range
			if (range) {
				userMin = min = max - range;
				userMax = max;
				if (secondPass) {
					range = null;  // don't use it when running setExtremes
				}
			}

			// maxZoom exceeded, just center the selection
			if (max - min &lt; maxZoom) {
				zoomOffset = (maxZoom - max + min) / 2;
				// if min and max options have been set, don't go beyond it
				min = mathMax(min - zoomOffset, pick(options.min, min - zoomOffset),
 dataMin);
				max = mathMin(min + maxZoom, pick(options.max, min + maxZoom), 
dataMax);
			}

			// pad the values to get clear of the chart's edges
			if (!categories &amp;&amp; !usePercentage &amp;&amp; !isLinked 
&amp;&amp; defined(min) &amp;&amp; defined(max)) {
				length = (max - min) || 1;
				if (!defined(options.min) &amp;&amp; !defined(userMin) &amp;&amp; 
minPadding &amp;&amp; (dataMin &lt; 0 || !ignoreMinPadding)) {
					min -= length * minPadding;
				}
				if (!defined(options.max) &amp;&amp; !defined(userMax)  &amp;&amp; 
maxPadding &amp;&amp; (dataMax &gt; 0 || !ignoreMaxPadding)) {
					max += length * maxPadding;
				}
			}

			// get tickInterval
			if (min === max || min === undefined || max === undefined) {
				tickInterval = 1;
			} else if (isLinked &amp;&amp; !tickIntervalOption &amp;&amp;
					tickPixelIntervalOption === linkedParent.options.tickPixelInterval)
 {
				tickInterval = linkedParent.tickInterval;
			} else {
				tickInterval = pick(
					tickIntervalOption,
					categories ? // for categoried axis, 1 is default, for linear axis 
use tickPix
						1 :
						(max - min) * tickPixelIntervalOption / (axisLength || 1)
				);
			}

			if (!isDatetimeAxis) { // linear
				magnitude = math.pow(10, mathFloor(math.log(tickInterval) / 
math.LN10));
				if (!defined(options.tickInterval)) {
					tickInterval = normalizeTickInterval(tickInterval, null, magnitude,
 options);
				}
			}
			axis.tickInterval = tickInterval; // record for linked axis

			// get minorTickInterval
			minorTickInterval = options.minorTickInterval === 'auto' &amp;&amp; 
tickInterval ?
					tickInterval / 5 : options.minorTickInterval;

			// find the tick positions
			if (isDatetimeAxis)	{
				tickPositions = getTimeTicks(tickInterval, min, max, 
options.startOfWeek);
				dateTimeLabelFormat = 
options.dateTimeLabelFormats[tickPositions.unit[0]];
			} else {
				setLinearTickPositions();
			}

			if (!isLinked) {
				// pad categorised axis to nearest half unit
				if (categories || padAxis) {
					catPad = (categories ? 1 : tickInterval) * 0.5;
					if (categories || !defined(pick(options.min, userMin))) {
						min -= catPad;
					}
					if (categories || !defined(pick(options.max, userMax))) {
						max += catPad;
					}
				}

				// reset min/max or remove extremes based on start/end on tick
				var roundedMin = tickPositions[0],
					roundedMax = tickPositions[tickPositions.length - 1];

				if (options.startOnTick) {
					min = roundedMin;
				} else if (min &gt; roundedMin) {
					tickPositions.shift();
				}

				if (options.endOnTick) {
					max = roundedMax;
				} else if (max &lt; roundedMax) {
					tickPositions.pop();
				}

				// record the greatest number of ticks for multi axis
				if (!maxTicks) { // first call, or maxTicks have been reset after a 
zoom operation
					maxTicks = {
						x: 0,
						y: 0
					};
				}

				if (!isDatetimeAxis &amp;&amp; tickPositions.length &gt; 
maxTicks[xOrY] &amp;&amp; options.alignTicks !== false) {
					maxTicks[xOrY] = tickPositions.length;
				}
			}


		}

		/**
		 * When using multiple axes, adjust the number of ticks to match the 
highest
		 * number of ticks in that group
		 */
		function adjustTickAmount() {

			if (maxTicks &amp;&amp; !isDatetimeAxis &amp;&amp; !categories 
&amp;&amp; !isLinked &amp;&amp; options.alignTicks !== false) { // only 
apply to linear scale
				var oldTickAmount = tickAmount,
					calculatedTickAmount = tickPositions.length;

				// set the axis-level tickAmount to use below
				tickAmount = maxTicks[xOrY];

				if (calculatedTickAmount &lt; tickAmount) {
					while (tickPositions.length &lt; tickAmount) {
						tickPositions.push( correctFloat(
							tickPositions[tickPositions.length - 1] + tickInterval
						));
					}
					transA *= (calculatedTickAmount - 1) / (tickAmount - 1);
					max = tickPositions[tickPositions.length - 1];

				}
				if (defined(oldTickAmount) &amp;&amp; tickAmount !== oldTickAmount) {
					axis.isDirty = true;
				}
			}

		}

		/**
		 * Set the scale based on data min and max, user set min and max or 
options
		 *
		 */
		function setScale() {
			var type,
				i;

			oldMin = min;
			oldMax = max;

			// get data extremes if needed
			getSeriesExtremes();

			// get fixed positions based on tickInterval
			setTickPositions();

			// the translation factor used in translate function
			oldTransA = transA;
			transA = axisLength / ((max - min) || 1);

			// reset stacks
			if (!isXAxis) {
				for (type in stacks) {
					for (i in stacks[type]) {
						stacks[type][i].cum = stacks[type][i].total;
					}
				}
			}

			// mark as dirty if it is not already set to dirty and extremes have changed
			if (!axis.isDirty) {
				axis.isDirty = (min !== oldMin || max !== oldMax);
			}

		}

		/**
		 * Set the extremes and optionally redraw
		 * @param {Number} newMin
		 * @param {Number} newMax
		 * @param {Boolean} redraw
		 * @param {Boolean|Object} animation Whether to apply animation, and optionally animation
		 *    configuration
		 *
		 */
		function setExtremes(newMin, newMax, redraw, animation) {
			var start = + new Date();
			redraw = pick(redraw, true); // defaults to true

			fireEvent(axis, 'setExtremes', { // fire an event to enable syncing of multiple charts
				min: newMin,
				max: newMax
			}, function() { // the default event handler

				userMin = newMin;
				userMax = newMax;


				// redraw
				if (redraw) {
					chart.redraw(animation);
				}
			});
		}

		/**
		 * Update the axis metrics
		 */
		function setAxisSize() {

			var offsetLeft = options.offsetLeft || 0,
				offsetRight = options.offsetRight || 0;

			// basic values
			axisLeft = pick(options.left, plotLeft + offsetLeft);
			axisTop = pick(options.top, plotTop);
			axisWidth = pick(options.width, plotWidth - offsetLeft + offsetRight);
			axisHeight = pick(options.height, plotHeight);
			axisBottom = chartHeight - axisHeight - axisTop;
			axisRight = chartWidth - axisWidth - axisLeft;

			// expose to use in Series obejct
			axis.left = axisLeft;
			axis.top = axisTop;

			// secondary values
			axisLength = horiz ? axisWidth : axisHeight;
			transA = axisLength / ((max - min) || 1);
			transB = horiz ? axisLeft : axisBottom; // translation addend
		}

		/**
		 * Get the actual axis extremes
		 */
		function getExtremes() {
			return {
				min: min,
				max: max,
				dataMin: dataMin,
				dataMax: dataMax,
				userMin: userMin,
				userMax: userMax
			};
		}

		/**
		 * Get the zero plane either based on zero or on the min or max value.
		 * Used in bar and area plots
		 */
		function getThreshold(threshold) {
			if (min &gt; threshold || threshold === null) {
				threshold = min;
			} else if (max &lt; threshold) {
				threshold = max;
			}

			return translate(threshold, 0, 1);
		}

		/**
		 * Add a plot band or plot line after render time
		 *
		 * @param options {Object} The plotBand or plotLine configuration object
		 */
		function addPlotBandOrLine(options) {
			var obj = new PlotLineOrBand(options).render();
			plotLinesAndBands.push(obj);
			return obj;
		}

		/**
		 * Render the tick labels to a preliminary position to get their sizes
		 */
		function getOffset() {

			var hasData = associatedSeries.length &amp;&amp; defined(min) &amp;&amp; defined(max),
				titleOffset = 0,
				titleMargin = 0,
				axisTitleOptions = options.title,
				labelOptions = options.labels,
				directionFactor = [-1, 1, 1, -1][side],
				n;

			if (!axisGroup) {
				axisGroup = renderer.g('axis')
					.attr({ zIndex: 7 })
					.add();
				gridGroup = renderer.g('grid')
					.attr({ zIndex: 1 })
					.add();
			}

			labelOffset = 0; // reset

			if (hasData || isLinked) {
				each(tickPositions, function(pos) {
					if (!ticks[pos]) {
						ticks[pos] = new Tick(pos);
					} else {
						ticks[pos].addLabel(); // update labels depending on tick interval
					}

				});

				each(tickPositions, function(pos) {
					ticks[pos].attachLabel();
				});
				each(tickPositions, function(pos) {
					ticks[pos].computeBBox();
				});
				each(tickPositions, function(pos) {
					ticks[pos].updateTransformLabel();
				});

				each(tickPositions,function(pos) {
					// left side must be align: right and right side must have align: left for labels
					if (side === 0 || side === 2 || { 1: 'left', 3: 'right' }[side] === labelOptions.align) {

						// get the highest offset
						labelOffset = mathMax(
							ticks[pos].getLabelSize(),
							labelOffset
						);
					}

				});

				if (staggerLines) {
					labelOffset += (staggerLines - 1) * 16;
				}

			} else { // doesn't have data
				for (n in ticks) {
					ticks[n].destroy();
					delete ticks[n];
				}
			}

			if (axisTitleOptions &amp;&amp; axisTitleOptions.text) {
				if (!axis.axisTitle) {
					axis.axisTitle = renderer.text(
						axisTitleOptions.text,
						0,
						0
					)
					.attr({
						zIndex: 7,
						rotation: axisTitleOptions.rotation || 0,
						align:
							axisTitleOptions.textAlign ||
							{ low: 'left', middle: 'center', high: 'right' }[axisTitleOptions.align]
					})
					.css(axisTitleOptions.style)
					.add();
				}

				titleOffset = axis.axisTitle.getBBox()[horiz ? 'height' : 'width'];
				titleMargin = pick(axisTitleOptions.margin, horiz ? 5 : 10);

			}

			// handle automatic or user set offset
			offset = directionFactor * pick(options.offset, axisOffset[side]);

			axisTitleMargin =
				labelOffset +
				(side !== 2 &amp;&amp; labelOffset &amp;&amp; directionFactor * options.labels[horiz ? 'y' : 'x']) +
				titleMargin;

			axisOffset[side] = mathMax(
				axisOffset[side],
				axisTitleMargin + titleOffset + directionFactor * offset
			);

		}

		/**
		 * Render the axis
		 */
		function render() {
			var axisTitleOptions = options.title,
				stackLabelOptions = options.stackLabels,
				alternateGridColor = options.alternateGridColor,
				lineWidth = options.lineWidth,
				lineLeft,
				lineTop,
				linePath,
				hasRendered = chart.hasRendered,
				slideInTicks = hasRendered &amp;&amp; defined(oldMin) &amp;&amp; !isNaN(oldMin),
				hasData = associatedSeries.length &amp;&amp; defined(min) &amp;&amp; defined(max);

			// If the series has data draw the ticks. Else only the line and title
			if (hasData || isLinked) {

				// minor ticks
				if (minorTickInterval &amp;&amp; !categories) {
					var pos = min + (tickPositions[0] - min) % minorTickInterval;
					for (pos; pos &lt;= max; pos += minorTickInterval) {
						if (!minorTicks[pos]) {
							minorTicks[pos] = new Tick(pos, true);
						}

						// render new ticks in old position
						if (slideInTicks &amp;&amp; minorTicks[pos].isNew) {
							minorTicks[pos].render(null, true);
						}


						minorTicks[pos].isActive = true;
						minorTicks[pos].render();
					}
				}

				// major ticks
				each(tickPositions, function(pos, i) {
					// linked axes need an extra check to find out if
					if (!isLinked || (pos &gt;= min &amp;&amp; pos &lt;= max)) {

						// render new ticks in old position
						if (slideInTicks &amp;&amp; ticks[pos].isNew) {
							ticks[pos].render(i, true);
						}

						ticks[pos].isActive = true;
						ticks[pos].render(i);
					}
				});

				// alternate grid color
				if (alternateGridColor) {
					each(tickPositions, function(pos, i) {
						if (i % 2 === 0 &amp;&amp; pos &lt; max) {
							if (!alternateBands[pos]) {
								alternateBands[pos] = new PlotLineOrBand();
							}
							alternateBands[pos].options = {
								from: pos,
								to: tickPositions[i + 1] !== UNDEFINED ? tickPositions[i + 1] : max,
								color: alternateGridColor
							};
							alternateBands[pos].render();
							alternateBands[pos].isActive = true;
						}
					});
				}

				// custom plot lines and bands
				if (!hasRendered) { // only first time
					each((options.plotLines || []).concat(options.plotBands || []), function(plotLineOptions) {
						plotLinesAndBands.push(new PlotLineOrBand(plotLineOptions).render());
					});
				}



			} // end if hasData

			// remove inactive ticks
			each([ticks, minorTicks, alternateBands], function(coll) {
				var pos;
				for (pos in coll) {
					if (!coll[pos].isActive) {
						coll[pos].destroy();
						delete coll[pos];
					} else {
						coll[pos].isActive = false; // reset
					}
				}
			});




			// Static items. As the axis group is cleared on subsequent calls
			// to render, these items are added outside the group.
			// axis line
			if (lineWidth) {
				lineLeft = axisLeft + (opposite ? axisWidth : 0) + offset;
				lineTop = chartHeight - axisBottom - (opposite ? axisHeight : 0) + offset;

				linePath = renderer.crispLine([
						M,
						horiz ?
							axisLeft:
							lineLeft,
						horiz ?
							lineTop:
							axisTop,
						L,
						horiz ?
							chartWidth - axisRight :
							lineLeft,
						horiz ?
							lineTop:
							chartHeight - axisBottom
					], lineWidth);
				if (!axisLine) {
					axisLine = renderer.path(linePath)
						.attr({
							stroke: options.lineColor,
							'stroke-width': lineWidth,
							zIndex: 7
						})
						.add();
				} else {
					axisLine.animate({ d: linePath });
				}

			}

			if (axis.axisTitle) {
				// compute anchor points for each of the title align options
				var margin = horiz ? axisLeft : axisTop,
					fontSize = pInt(axisTitleOptions.style.fontSize || 12),
				// the position in the length direction of the axis
				alongAxis = {
					low: margin + (horiz ? 0 : axisLength),
					middle: margin + axisLength / 2,
					high: margin + (horiz ? axisLength : 0)
				}[axisTitleOptions.align],

				// the position in the perpendicular direction of the axis
				offAxis = (horiz ? axisTop + axisHeight : axisLeft) +
					(horiz ? 1 : -1) * // horizontal axis reverses the margin
					(opposite ? -1 : 1) * // so does opposite axes
					axisTitleMargin +
					(side === 2 ? fontSize : 0);

				axis.axisTitle[hasRendered ? 'animate' : 'attr']({
					x: horiz ?
						alongAxis:
						offAxis + (opposite ? axisWidth : 0) + offset +
							(axisTitleOptions.x || 0), // x
					y: horiz ?
						offAxis - (opposite ? axisHeight : 0) + offset:
						alongAxis + (axisTitleOptions.y || 0) // y
				});

			}

			// Stacked totals:
			if (stackLabelOptions &amp;&amp; stackLabelOptions.enabled) {
				var stackKey, oneStack, stackCategory,
					stackTotalGroup = axis.stackTotalGroup;

				// Create a separate group for the stack total labels
				if (!stackTotalGroup) {
					axis.stackTotalGroup = stackTotalGroup =
						renderer.g('stack-labels')
							.attr({
								visibility: VISIBLE,
								zIndex: 6
							})
							.translate(plotLeft, plotTop)
							.add();
				}

				// Render each stack total
				for (stackKey in stacks) {
					oneStack = stacks[stackKey];
					for (stackCategory in oneStack) {
						oneStack[stackCategory].render(stackTotalGroup);
					}
				}
			}
			// End stacked totals

			axis.isDirty = false;
		}

		/**
		 * Remove a plot band or plot line from the chart by id
		 * @param {Object} id
		 */
		function removePlotBandOrLine(id) {
			var i = plotLinesAndBands.length;
			while (i--) {
				if (plotLinesAndBands[i].id === id) {
					plotLinesAndBands[i].destroy();
				}
			}
		}

		/**
		 * Redraw the axis to reflect changes in the data or axis extremes
		 */
		function redraw() {

			// hide tooltip and hover states
			if (tracker.resetTracker) {
				tracker.resetTracker();
			}

			// render the axis
			render();

			// move plot lines and bands
			each(plotLinesAndBands, function(plotLine) {
				plotLine.render();
			});

			// mark associated series as dirty and ready for redraw
			each(associatedSeries, function(series) {
				series.isDirty = true;
			});

		}

		/**
		 * Set new axis categories and optionally redraw
		 * @param {Array} newCategories
		 * @param {Boolean} doRedraw
		 */
		function setCategories(newCategories, doRedraw) {
				// set the categories
				axis.categories = categories = newCategories;

				// force reindexing tooltips
				each(associatedSeries, function(series) {
					series.translate();
					series.setTooltipPoints(true);
				});


				// optionally redraw
				axis.isDirty = true;

				if (pick(doRedraw, true)) {
					chart.redraw();
				}
		}



		// Run Axis

		// Register
		axes.push(axis);
		chart[isXAxis ? 'xAxis' : 'yAxis'].push(axis);

		// inverted charts have reversed xAxes as default
		if (inverted &amp;&amp; isXAxis &amp;&amp; reversed === UNDEFINED) {
			reversed = true;
		}


		// expose some variables
		extend(axis, {
			addPlotBand: addPlotBandOrLine,
			addPlotLine: addPlotBandOrLine,
			adjustTickAmount: adjustTickAmount,
			categories: categories,
			getExtremes: getExtremes,
			getPlotLinePath: getPlotLinePath,
			getThreshold: getThreshold,
			isXAxis: isXAxis,
			options: options,
			plotLinesAndBands: plotLinesAndBands,
			getOffset: getOffset,
			render: render,
			setAxisSize: setAxisSize,
			setCategories: setCategories,
			setExtremes: setExtremes,
			setScale: setScale,
			setTickPositions: setTickPositions,
			translate: translate,
			redraw: redraw,
			removePlotBand: removePlotBandOrLine,
			removePlotLine: removePlotBandOrLine,
			reversed: reversed,
			stacks: stacks
		});

		// register event listeners
		for (eventType in events) {
			addEvent(axis, eventType, events[eventType]);
		}

		// set min and max
		setScale();

	} // end Axis


	/**
	 * The toolbar object
	 *
	 * @param {Object} chart
	 */
	function Toolbar(chart) {
		var buttons = {};

		function add(id, text, title, fn) {
			if (!buttons[id]) {
				var button = renderer.text(
					text,
					0,
					0
				)
				.css(options.toolbar.itemStyle)
				.align({
					align: 'right',
					x: - marginRight - 20,
					y: plotTop + 30
				})
				.on('click', fn)
				.attr({
					align: 'right',
					zIndex: 20
				})
				.add();
				buttons[id] = button;
			}
		}
		function remove(id) {
			discardElement(buttons[id].element);
			buttons[id] = null;
		}

		// public
		return {
			add: add,
			remove: remove
		};
	}

	/**
	 * The tooltip object
	 * @param {Object} options Tooltip options
	 */
	function Tooltip (options) {
		var currentSeries,
			borderWidth = options.borderWidth,
			crosshairsOptions = options.crosshairs,
			crosshairs = [],
			style = options.style,
			shared = options.shared,
			padding = pInt(style.padding),
			boxOffLeft = borderWidth + padding, // off left/top position as IE can't
				//properly handle negative positioned shapes
			tooltipIsHidden = true,
			boxWidth,
			boxHeight,
			currentX = 0,
			currentY = 0;

		// remove padding CSS and apply padding on box instead
		style.padding = 0;

		// create the label
		var label = renderer.label('', 0, 0)
			.attr({
				padding: padding,
				fill: options.backgroundColor,
				'stroke-width': borderWidth,
				r: options.borderRadius,
				zIndex: 8
			})
			.css(style)
			.hide()
			.add()
			.shadow(1);

		/**
		 * In case no user defined formatter is given, this will be used
		 */
		function defaultFormatter() {
			var pThis = this,
				items = pThis.points || splat(pThis),
				xAxis = items[0].series.xAxis,
				x = pThis.x,
				isDateTime = xAxis &amp;&amp; xAxis.options.type === 'datetime',
				useHeader = isString(x) || isDateTime,
				series = items[0].series,
				headerFormat = series.tooltipHeaderFormat || '%A, %b %e, %Y',
				s;

			// build the header
			s = useHeader ?
			['<span style="font-size: 10px;">' +
				(isDateTime ? dateFormat('%A, %b %e, %Y', x) :  x) +
				'</span>'] : [];

			// build the values
			each(items, function(item) {
				series = item.series;
				s.push((series.tooltipFormatter &amp;&amp; series.tooltipFormatter(item)) ||
					item.point.tooltipFormatter(useHeader));
			});
			return s.join('<br>');
		}

		/**
		 * Provide a soft movement for the tooltip
		 *
		 * @param {Number} finalX
		 * @param {Number} finalY
		 */
		function move(finalX, finalY) {

			currentX = tooltipIsHidden ? finalX : (2 * currentX + finalX) / 3;
			currentY = tooltipIsHidden ? finalY : (currentY + finalY) / 2;

			//group.translate(currentX, currentY);
			label.attr({ x: currentX, y: currentY });


			// run on next tick of the mouse tracker
			if (mathAbs(finalX - currentX) &gt; 1 || mathAbs(finalY - currentY) &gt; 1) {
				tooltipTick = function() {
					move(finalX, finalY);
				};
			} else {
				tooltipTick = null;
			}
		}

		/**
		 * Hide the tooltip
		 */
		function hide() {
			if (!tooltipIsHidden) {
				var hoverPoints = chart.hoverPoints;

				//group.hide();
				label.hide();

				each(crosshairs, function(crosshair) {
					if (crosshair) {
						crosshair.hide();
					}
				});

				// hide previous hoverPoints and set new
				if (hoverPoints) {
					each(hoverPoints, function(point) {
						point.setState();
					});
				}
				chart.hoverPoints = null;


				tooltipIsHidden = true;
			}

		}

		/**
		 * Refresh the tooltip's text and position.
		 * @param {Object} point
		 *
		 */
		function refresh(point) {
			var x,
				y,
				boxX,
				boxY,
				show,
				bBox,
				plotX,
				plotY,
				textConfig = {},
				text,
				pointConfig = [],
				tooltipPos = point.tooltipPos,
				formatter = options.formatter || defaultFormatter,
				hoverPoints = chart.hoverPoints;

			// shared tooltip, array is sent over
			if (shared &amp;&amp; !(point.series &amp;&amp; point.series.noSharedTooltip)) {
				plotY = 0;

				// hide previous hoverPoints and set new
				if (hoverPoints) {
					each(hoverPoints, function(point) {
						point.setState();
					});
				}
				chart.hoverPoints = point;

				each(point, function(item, i) {
					item.setState(HOVER_STATE);
					plotY += item.plotY; // for average

					pointConfig.push(item.getLabelConfig());
				});

				plotX = point[0].plotX;
				plotY = mathRound(plotY) / point.length; // mathRound because Opera 10 has problems here

				textConfig = {
					x: point[0].category
				};
				textConfig.points = pointConfig;
				point = point[0];

			// single point tooltip
			} else {
				textConfig = point.getLabelConfig();
			}
			text = formatter.call(textConfig);

			// register the current series
			currentSeries = point.series;

			// get the reference point coordinates (pie charts use tooltipPos)
			plotX = pick(plotX, point.plotX);
			plotY = pick(plotY, point.plotY);

			x = mathRound(tooltipPos ? tooltipPos[0] : (inverted ? plotWidth - plotY : plotX));
			y = mathRound(tooltipPos ? tooltipPos[1] : (inverted ? plotHeight - plotX : plotY));


			// hide tooltip if the point falls outside the plot
			show = shared || !point.series.isCartesian || isInsidePlot(x, y);

			// update the inner HTML
			if (text === false || !show) {
				hide();
			} else {

				// show it
				if (tooltipIsHidden) {
					label.show();
					tooltipIsHidden = false;
				}

				// update text
				label.attr({
					text: text
				});

				// get the bounding box
				bBox = label.getBBox();
				boxWidth = bBox.width;
				boxHeight = bBox.height;

				// set the size of the box
				label.attr({
					stroke: options.borderColor || point.color || currentSeries.color || '#606060'
				});

				// keep the box within the chart area
				boxX = x - boxWidth + plotLeft - 25;
				boxY = y - boxHeight + plotTop + 10;

				// it is too far to the left, adjust it
				if (boxX &lt; 7) {
					boxX = 7;
					boxY -= 30;
				}


				if (boxY &lt; plotTop + 5) {
					boxY = plotTop + 5; // above
				} else if (boxY + boxHeight &gt; chartHeight) {
					boxY = chartHeight - boxHeight - 5; // below
				}

				// do the move
				move(mathRound(boxX - boxOffLeft), mathRound(boxY - boxOffLeft));


			}


			// crosshairs
			if (crosshairsOptions) {
				crosshairsOptions = splat(crosshairsOptions); // [x, y]

				var path,
					i = crosshairsOptions.length,
					attribs,
					axis;

				while (i--) {
					axis = point.series[i ? 'yAxis' : 'xAxis'];
					if (crosshairsOptions[i] &amp;&amp; axis) {
						path = axis
							.getPlotLinePath(point[i ? 'y' : 'x'], 1);
						if (crosshairs[i]) {
							crosshairs[i].attr({ d: path, visibility: VISIBLE });

						} else {
							attribs = {
								'stroke-width': crosshairsOptions[i].width || 1,
								stroke: crosshairsOptions[i].color || '#C0C0C0',
								zIndex: 2
							};
							if (crosshairsOptions[i].dashStyle) {
								attribs.dashstyle = crosshairsOptions[i].dashStyle;
							}
							crosshairs[i] = renderer.path(path)
								.attr(attribs)
								.add();
						}
					}
				}
			}
		}



		// public members
		return {
			shared: shared,
			refresh: refresh,
			hide: hide
		};
	}

	/**
	 * The mouse tracker object
	 * @param {Object} chart
	 * @param {Object} options
	 */
	function MouseTracker (chart, options) {


		var mouseDownX,
			mouseDownY,
			hasDragged,
			selectionMarker,
			zoomType = optionsChart.zoomType,
			zoomX = /x/.test(zoomType),
			zoomY = /y/.test(zoomType),
			zoomHor = (zoomX &amp;&amp; !inverted) || (zoomY &amp;&amp; inverted),
			zoomVert = (zoomY &amp;&amp; !inverted) || (zoomX &amp;&amp; inverted);

		/**
		 * Add crossbrowser support for chartX and chartY
		 * @param {Object} e The event object in standard browsers
		 */
		function normalizeMouseEvent(e) {
			var ePos,
				pageZoomFix = isWebKit &amp;&amp; doc.width / doc.documentElement.clientWidth - 1,
				chartPosLeft,
				chartPosTop,
				chartX,
				chartY;

			// common IE normalizing
			e = e || win.event;
			if (!e.target) {
				e.target = e.srcElement;
			}

			// jQuery only copies over some properties. IE needs e.x and iOS needs touches.
			if (e.originalEvent) {
				e = e.originalEvent;
			}

			// iOS
			ePos = e.touches ? e.touches.item(0) : e;

			// in certain cases, get mouse position
			if (e.type !== 'mousemove' || win.opera || pageZoomFix) { // only Opera needs position on mouse move, see below
				chartPosition = getPosition(container);
				chartPosLeft = chartPosition.left;
				chartPosTop = chartPosition.top;
			}

			// chartX and chartY
			if (isIE) { // IE including IE9 that has pageX but in a different meaning
				chartX = e.x;
				chartY = e.y;
			} else {
				if (ePos.layerX === UNDEFINED) { // Opera and iOS
					chartX = ePos.pageX - chartPosLeft;
					chartY = ePos.pageY - chartPosTop;
				} else {
					chartX = e.layerX;
					chartY = e.layerY;
				}
			}

			// correct for page zoom bug in WebKit
			if (pageZoomFix) {
				chartX += mathRound((pageZoomFix + 1) * chartPosLeft - chartPosLeft);
				chartY += mathRound((pageZoomFix + 1) * chartPosTop - chartPosTop);
			}

			return extend(e, {
				chartX: chartX,
				chartY: chartY
			});
		}

		/**
		 * Get the click position in terms of axis values.
		 *
		 * @param {Object} e A mouse event
		 */
		function getMouseCoordinates(e) {
			var coordinates = {
				xAxis: [],
				yAxis: []
			};
			each(axes, function(axis, i) {
				var translate = axis.translate,
					isXAxis = axis.isXAxis,
					isHorizontal = inverted ? !isXAxis : isXAxis;

				coordinates[isXAxis ? 'xAxis' : 'yAxis'].push({
					axis: axis,
					value: translate(
						isHorizontal ?
							e.chartX - plotLeft  :
							plotHeight - e.chartY + plotTop,
						true
					)
				});
			});
			return coordinates;
		}

		/**
		 * With line type charts with a single tracker, get the point closest to the mouse
		 */
		function onmousemove (e) {
			var point,
				points,
				hoverPoint = chart.hoverPoint,
				hoverSeries = chart.hoverSeries,
				i,
				j,
				distance = chartWidth,
				index = inverted ? e.chartY : e.chartX - plotLeft; // wtf?

			// shared tooltip
			if (tooltip &amp;&amp; options.shared &amp;&amp; !(hoverSeries &amp;&amp; hoverSeries.noSharedTooltip)) {
				points = [];

				// loop over all series and find the ones with points closest to the mouse
				i = series.length;
				for (j = 0; j &lt; i; j++) {
					if (series[j].visible &amp;&amp; series[j].tooltipPoints.length &amp;&amp;
							series[j].options.enableMouseTracking !== false &amp;&amp;
							!series[j].noSharedTooltip) {
						point = series[j].tooltipPoints[index];
						point._dist = mathAbs(index - point.plotX);
						distance = mathMin(distance, point._dist);
						points.push(point);
					}
				}
				// remove furthest points
				i = points.length;
				while (i--) {
					if (points[i]._dist &gt; distance) {
						points.splice(i, 1);
					}
				}
				// refresh the tooltip if necessary
				if (points.length &amp;&amp; (points[0].plotX !== hoverX)) {
					tooltip.refresh(points);
					hoverX = points[0].plotX;
				}
			}

			// separate tooltip and general mouse events
			if (hoverSeries &amp;&amp; hoverSeries.tracker) { // only use for line-type series with common tracker

				// get the point
				point = hoverSeries.tooltipPoints[index];

				// a new point is hovered, refresh the tooltip
				if (point &amp;&amp; point !== hoverPoint) {

					// trigger the events
					point.onMouseOver();

				}
			}
		}



		/**
		 * Reset the tracking by hiding the tooltip, the hover series state and the hover point
		 */
		function resetTracker() {
			var hoverSeries = chart.hoverSeries,
				hoverPoint = chart.hoverPoint;

			if (hoverPoint) {
				hoverPoint.onMouseOut();
			}

			if (hoverSeries) {
				hoverSeries.onMouseOut();
			}

			if (tooltip) {
				tooltip.hide();
			}

			hoverX = null;
		}

		/**
		 * Mouse up or outside the plot area
		 */
		function drop() {
			if (selectionMarker) {
				var selectionData = {
						xAxis: [],
						yAxis: []
					},
					selectionBox = selectionMarker.getBBox(),
					selectionLeft = selectionBox.x - plotLeft,
					selectionTop = selectionBox.y - plotTop;


				// a selection has been made
				if (hasDragged) {

					// record each axis' min and max
					each(axes, function(axis, i) {
						if (axis.options.zoomEnabled !== false) {
							var translate = axis.translate,
								isXAxis = axis.isXAxis,
								isHorizontal = inverted ? !isXAxis : isXAxis,
								selectionMin = translate(
									isHorizontal ?
										selectionLeft :
										plotHeight - selectionTop - selectionBox.height,
									true,
									0,
									0,
									1
								),
								selectionMax = translate(
									isHorizontal ?
										selectionLeft + selectionBox.width :
										plotHeight - selectionTop,
									true,
									0,
									0,
									1
								);

								selectionData[isXAxis ? 'xAxis' : 'yAxis'].push({
									axis: axis,
									min: mathMin(selectionMin, selectionMax), // for reversed axes,
									max: mathMax(selectionMin, selectionMax)
								});
						}
					});
					fireEvent(chart, 'selection', selectionData, zoom);

				}
				selectionMarker = selectionMarker.destroy();
			}

			css(container, { cursor: 'auto' });

			chart.mouseIsDown = mouseIsDown = hasDragged = false;
			removeEvent(doc, hasTouch ? 'touchend' : 'mouseup', drop);

		}

		/**
		 * Set the JS events on the container element
		 */
		function setDOMEvents () {
			var lastWasOutsidePlot = true;

			/*
			 * Record the starting position of a dragoperation
			 */
			container.onmousedown = function(e) {
				e = normalizeMouseEvent(e);

				// record the start position
				//e.preventDefault &amp;&amp; e.preventDefault();

				chart.mouseIsDown = mouseIsDown = true;
				mouseDownX = e.chartX;
				mouseDownY = e.chartY;

				addEvent(doc, hasTouch ? 'touchend' : 'mouseup', drop);
			};

			// The mousemove, touchmove and touchstart event handler
			var mouseMove = function(e) {

				// let the system handle multitouch operations like two finger scroll
				// and pinching
				if (e &amp;&amp; e.touches &amp;&amp; e.touches.length &gt; 1) {
					return;
				}

				// normalize
				e = normalizeMouseEvent(e);
				if (!hasTouch) { // not for touch devices
					e.returnValue = false;
				}

				var chartX = e.chartX,
					chartY = e.chartY,
					isOutsidePlot = !isInsidePlot(chartX - plotLeft, chartY - plotTop);

				// on touch devices, only trigger click if a handler is defined
				if (hasTouch &amp;&amp; e.type === 'touchstart') {
					if (attr(e.target, 'isTracker')) {
						if (!chart.runTrackerClick) {
							e.preventDefault();
						}
					} else if (!runChartClick &amp;&amp; !isOutsidePlot) {
						e.preventDefault();
					}
				}

				// cancel on mouse outside
				if (isOutsidePlot) {

					if (!lastWasOutsidePlot) {
						// reset the tracker
						resetTracker();
					}

					// drop the selection if any and reset mouseIsDown and hasDragged
					//drop();
					if (chartX &lt; plotLeft) {
						chartX = plotLeft;
					} else if (chartX &gt; plotLeft + plotWidth) {
						chartX = plotLeft + plotWidth;
					}

					if (chartY &lt; plotTop) {
						chartY = plotTop;
					} else if (chartY &gt; plotTop + plotHeight) {
						chartY = plotTop + plotHeight;
					}

				}

				if (mouseIsDown &amp;&amp; e.type !== 'touchstart') { // make selection

					// determine if the mouse has moved more than 10px
					hasDragged = Math.sqrt(
						Math.pow(mouseDownX - chartX, 2) +
						Math.pow(mouseDownY - chartY, 2));
					if (hasDragged &gt; 10) {
						var clickedInside = isInsidePlot(mouseDownX - plotLeft, mouseDownY - plotTop);

						// make a selection
						if (hasCartesianSeries &amp;&amp; (zoomX || zoomY) &amp;&amp; clickedInside) {
							if (!selectionMarker) {
								selectionMarker = renderer.rect(
									plotLeft,
									plotTop,
									zoomHor ? 1 : plotWidth,
									zoomVert ? 1 : plotHeight,
									0
								)
								.attr({
									fill: 'rgba(69,114,167,0.25)',
									zIndex: 7
								})
								.add();
							}
						}

						// adjust the width of the selection marker
						if (selectionMarker &amp;&amp; zoomHor) {
							var xSize = chartX - mouseDownX;
							selectionMarker.attr({
								width: mathAbs(xSize),
								x: (xSize &gt; 0 ? 0 : xSize) + mouseDownX
							});
						}
						// adjust the height of the selection marker
						if (selectionMarker &amp;&amp; zoomVert) {
							var ySize = chartY - mouseDownY;
							selectionMarker.attr({
								height: mathAbs(ySize),
								y: (ySize &gt; 0 ? 0 : ySize) + mouseDownY
							});
						}

						// panning
						if (clickedInside &amp;&amp; !selectionMarker &amp;&amp; optionsChart.panning) {
							var xAxis = chart.xAxis[0],
								extremes = xAxis.getExtremes(),
								dataMin = extremes.dataMin,
								dataMax = extremes.dataMax,
								newMin = xAxis.translate(mouseDownX - chartX, true),
								newMax = xAxis.translate(mouseDownX + plotWidth - chartX, true);

							if (newMin &gt; dataMin &amp;&amp; newMax &lt; dataMax) {
								xAxis.setExtremes(newMin, newMax, true, false);
							}

							mouseDownX = chartX;
							css(container, { cursor: 'move' });
						}
					}

				} else if (!isOutsidePlot) {
					// show the tooltip
					onmousemove(e);
				}

				lastWasOutsidePlot = isOutsidePlot;

				// when outside plot, allow touch-drag by returning true
				return isOutsidePlot || !hasCartesianSeries;
			};

			/*
			 * When the mouse enters the container, run mouseMove
			 */
			container.onmousemove = mouseMove;

			/*
			 * When the mouse leaves the container, hide the tracking (tooltip).
			 */
			addEvent(container, 'mouseleave', resetTracker);


			container.ontouchstart = function(e) {
				// For touch devices, use touchmove to zoom
				if (zoomX || zoomY) {
					container.onmousedown(e);
				}
				// Show tooltip and prevent the lower mouse pseudo event
				mouseMove(e);
			};

			/*
			 * Allow dragging the finger over the chart to read the values on touch
			 * devices
			 */
			container.ontouchmove = mouseMove;

			/*
			 * Allow dragging the finger over the chart to read the values on touch
			 * devices
			 */
			container.ontouchend = function() {
				if (hasDragged) {
					resetTracker();
				}
			};


			// MooTools 1.2.3 doesn't fire this in IE when using addEvent
			container.onclick = function(e) {
				var hoverPoint = chart.hoverPoint;
				e = normalizeMouseEvent(e);

				e.cancelBubble = true; // IE specific


				if (!hasDragged) {
					if (hoverPoint &amp;&amp; attr(e.target, 'isTracker')) {
						var plotX = hoverPoint.plotX,
							plotY = hoverPoint.plotY;

						// add page position info
						extend(hoverPoint, {
							pageX: chartPosition.left + plotLeft +
								(inverted ? plotWidth - plotY : plotX),
							pageY: chartPosition.top + plotTop +
								(inverted ? plotHeight - plotX : plotY)
						});

						// the series click event
						fireEvent(hoverPoint.series, 'click', extend(e, {
							point: hoverPoint
						}));

						// the point click event
						hoverPoint.firePointEvent('click', e);

					} else {
						extend(e, getMouseCoordinates(e));

						// fire a click event in the chart
						if (isInsidePlot(e.chartX - plotLeft, e.chartY - plotTop)) {
							fireEvent(chart, 'click', e);
						}
					}


				}
				// reset mouseIsDown and hasDragged
				hasDragged = false;
			};

		}

		/**
		 * Create the image map that listens for mouseovers
		 */
		placeTrackerGroup = function() {

			// first create - plot positions is not final at this stage
			if (!trackerGroup) {
				chart.trackerGroup = trackerGroup = renderer.g('tracker')
					.attr({ zIndex: 9 })
					.add();

			// then position - this happens on load and after resizing and changing
			// axis or box positions
			} else {
				trackerGroup.translate(plotLeft, plotTop);
				if (inverted) {
					trackerGroup.attr({
						width: chart.plotWidth,
						height: chart.plotHeight
					}).invert();
				}
			}
		};


		// Run MouseTracker
		placeTrackerGroup();
		if (options.enabled) {
			chart.tooltip = tooltip = Tooltip(options);
		}

		setDOMEvents();

		// set the fixed interval ticking for the smooth tooltip
		tooltipInterval = setInterval(function() {
			if (tooltipTick) {
				tooltipTick();
			}
		}, 32);

		// expose properties
		extend(this, {
			zoomX: zoomX,
			zoomY: zoomY,
			resetTracker: resetTracker,
			normalizeMouseEvent: normalizeMouseEvent
		});
	}



	/**
	 * The overview of the chart's series
	 * @param {Object} chart
	 */
	var Legend = function(chart) {

		var options = chart.options.legend;

		if (!options.enabled) {
			return;
		}

		var horizontal = options.layout === 'horizontal',
			symbolWidth = options.symbolWidth,
			symbolPadding = options.symbolPadding,
			allItems,
			style = options.style,
			itemStyle = options.itemStyle,
			itemHoverStyle = options.itemHoverStyle,
			itemHiddenStyle = options.itemHiddenStyle,
			padding = pInt(style.padding),
			rightPadding = 20,
			y = 18,
			initialItemX = 4 + padding + symbolWidth + symbolPadding,
			itemX,
			itemY,
			lastItemY,
			itemHeight = 0,
			box,
			legendBorderWidth = options.borderWidth,
			legendBackgroundColor = options.backgroundColor,
			legendGroup,
			offsetWidth,
			widthOption = options.width,
			series = chart.series,
			reversedLegend = options.reversed;



		/**
		 * Set the colors for the legend item
		 * @param {Object} item A Series or Point instance
		 * @param {Object} visible Dimmed or colored
		 */
		function colorizeItem(item, visible) {
			var legendItem = item.legendItem,
				legendLine = item.legendLine,
				legendSymbol = item.legendSymbol,
				hiddenColor = itemHiddenStyle.color,
				textColor = visible ? options.itemStyle.color : hiddenColor,
				symbolColor = visible ? item.color : hiddenColor;

			if (legendItem) {
				legendItem.css({ fill: textColor });
			}
			if (legendLine) {
				legendLine.attr({ stroke: symbolColor });
			}
			if (legendSymbol) {
				legendSymbol.attr({
					stroke: symbolColor,
					fill: symbolColor
				});
			}
		}

		/**
		 * Position the legend item
		 * @param {Object} item A Series or Point instance
		 * @param {Object} visible Dimmed or colored
		 */
		function positionItem(item, itemX, itemY) {
			var legendItem = item.legendItem,
				legendLine = item.legendLine,
				legendSymbol = item.legendSymbol,
				checkbox = item.checkbox;
			if (legendItem) {
				legendItem.attr({
					x: itemX,
					y: itemY
				});
			}
			if (legendLine) {
				legendLine.translate(itemX, itemY - 4);
			}
			if (legendSymbol) {
				legendSymbol.attr({
					x: itemX + legendSymbol.xOff,
					y: itemY + legendSymbol.yOff
				});
			}
			if (checkbox) {
				checkbox.x = itemX;
				checkbox.y = itemY;
			}
		}

		/**
		 * Destroy a single legend item
		 * @param {Object} item The series or point
		 */
		function destroyItem(item) {
			var checkbox = item.checkbox;

			// destroy SVG elements
			each(['legendItem', 'legendLine', 'legendSymbol'], function(key) {
				if (item[key]) {
					item[key].destroy();
				}
			});

			if (checkbox) {
				discardElement(item.checkbox);
			}


		}


		/**
		 * Position the checkboxes after the width is determined
		 */
		function positionCheckboxes() {
			each(allItems, function(item) {
				var checkbox = item.checkbox,
					alignAttr = legendGroup.alignAttr;
				if (checkbox) {
					css(checkbox, {
						left: (alignAttr.translateX + item.legendItemWidth + checkbox.x - 40) +PX,
						top: (alignAttr.translateY + checkbox.y - 11) + PX
					});
				}
			});
		}

		/**
		 * Render a single specific legend item
		 * @param {Object} item A series or point
		 */
		function renderItem(item) {
			var bBox,
				itemWidth,
				legendSymbol,
				symbolX,
				symbolY,
				attribs,
				simpleSymbol,
				radius,
				li = item.legendItem,
				series = item.series || item,
				i = allItems.length,
				itemOptions = series.options,
				strokeWidth = (itemOptions &amp;&amp; itemOptions.borderWidth) || 0;


			if (!li) { // generate it once, later move it

				// let these series types use a simple symbol
				simpleSymbol = /^(bar|pie|area|column)$/.test(series.type);

				// generate the list item text
				item.legendItem = li = renderer.text(
						options.labelFormatter.call(item),
						0,
						0
					)
					.css(item.visible ? itemStyle : itemHiddenStyle)
					.on('mouseover', function() {
						item.setState(HOVER_STATE);
						li.css(itemHoverStyle);
					})
					.on('mouseout', function() {
						li.css(item.visible ? itemStyle : itemHiddenStyle);
						item.setState();
					})
					.on('click', function(event) {
						var strLegendItemClick = 'legendItemClick',
							fnLegendItemClick = function() {
								item.setVisible();
							};

						// click the name or symbol
						if (item.firePointEvent) { // point
							item.firePointEvent(strLegendItemClick, null, fnLegendItemClick);
						} else {
							fireEvent(item, strLegendItemClick, null, fnLegendItemClick);
						}
					})
					.attr({ zIndex: 2 })
					.add(legendGroup);

				// draw the line
				if (!simpleSymbol &amp;&amp; itemOptions &amp;&amp; itemOptions.lineWidth) {
					var attrs = {
							'stroke-width': itemOptions.lineWidth,
							zIndex: 2
						};
					if (itemOptions.dashStyle) {
						attrs.dashstyle = itemOptions.dashStyle;
					}
					item.legendLine = renderer.path([
						M,
						-symbolWidth - symbolPadding,
						0,
						L,
						-symbolPadding,
						0
					])
					.attr(attrs)
					.add(legendGroup);
				}

				// draw a simple symbol
				if (simpleSymbol) { // bar|pie|area|column

					legendSymbol = renderer.rect(
						(symbolX = -symbolWidth - symbolPadding),
						(symbolY = -11),
						symbolWidth,
						12,
						2
					).attr({
						//'stroke-width': 0,
						zIndex: 3
					}).add(legendGroup);
				}

				// draw the marker
				else if (itemOptions &amp;&amp; itemOptions.marker &amp;&amp; itemOptions.marker.enabled) {
					radius = itemOptions.marker.radius;
					legendSymbol = renderer.symbol(
						item.symbol,
						(symbolX = -symbolWidth / 2 - symbolPadding - radius),
						(symbolY = -4 - radius),
						2 * radius,
						2 * radius
					)
					.attr(item.pointAttr[NORMAL_STATE])
					.attr({ zIndex: 3 })
					.add(legendGroup);

				}
				if (legendSymbol) {
					legendSymbol.xOff = symbolX + (strokeWidth % 2 / 2);
					legendSymbol.yOff = symbolY + (strokeWidth % 2 / 2);
				}

				item.legendSymbol = legendSymbol;

				// colorize the items
				colorizeItem(item, item.visible);


				// add the HTML checkbox on top
				if (itemOptions &amp;&amp; itemOptions.showCheckbox) {
					item.checkbox = createElement('input', {
						type: 'checkbox',
						checked: item.selected,
						defaultChecked: item.selected // required by IE7
					}, options.itemCheckboxStyle, container);

					addEvent(item.checkbox, 'click', function(event) {
						var target = event.target;
						fireEvent(item, 'checkboxClick', {
								checked: target.checked
							},
							function() {
								item.select();
							}
						);
					});
				}
			}


			// calculate the positions for the next line
			bBox = li.getBBox();

			itemWidth = item.legendItemWidth =
				options.itemWidth || symbolWidth + symbolPadding + bBox.width + rightPadding;
			itemHeight = bBox.height;

			// if the item exceeds the width, start a new line
			if (horizontal &amp;&amp; itemX - initialItemX + itemWidth &gt;
					(widthOption || (chartWidth - 2 * padding - initialItemX))) {
				itemX = initialItemX;
				itemY += itemHeight;
			}
			lastItemY = itemY;

			// position the newly generated or reordered items
			positionItem(item, itemX, itemY);

			// advance
			if (horizontal)  {
				itemX += itemWidth;
			} else {
				itemY += itemHeight;
			}

			// the width of the widest item
			offsetWidth = widthOption || mathMax(
				horizontal ? itemX - initialItemX : itemWidth,
				offsetWidth
			);



			// add it all to an array to use below
			//allItems.push(item);
		}

		/**
		 * Render the legend. This method can be called both before and after
		 * chart.render. If called after, it will only rearrange items instead
		 * of creating new ones.
		 */
		function renderLegend() {
			itemX = initialItemX;
			itemY = y;
			offsetWidth = 0;
			lastItemY = 0;

			if (!legendGroup) {
				legendGroup = renderer.g('legend')
					.attr({ zIndex: 7 })
					.add();
			}


			// add each series or point
			allItems = [];
			each(series, function(serie) {
				var seriesOptions = serie.options;

				if (!seriesOptions.showInLegend) {
					return;
				}

				// use points or series for the legend item depending on legendType
				allItems = allItems.concat(seriesOptions.legendType === 'point' ?
					serie.data :
					serie
				);

			});

			// sort by legendIndex
			allItems.sort(function(a, b) {
				return (a.options.legendIndex || 0) - (b.options.legendIndex || 0);
			});

			// reversed legend
			if (reversedLegend) {
				allItems.reverse();
			}

			// render the items
			each(allItems, renderItem);


			// Draw the border
			legendWidth = widthOption || offsetWidth;
			legendHeight = lastItemY - y + itemHeight;

			if (legendBorderWidth || legendBackgroundColor) {
				legendWidth += 2 * padding;
				legendHeight += 2 * padding;

				if (!box) {
					box = renderer.rect(
						0,
						0,
						legendWidth,
						legendHeight,
						options.borderRadius,
						legendBorderWidth || 0
					).attr({
						stroke: options.borderColor,
						'stroke-width': legendBorderWidth || 0,
						fill: legendBackgroundColor || NONE
					})
					.add(legendGroup)
					.shadow(options.shadow);

				} else if (legendWidth &gt; 0 &amp;&amp; legendHeight &gt; 0) {
					box.animate(
						box.crisp(null, null, null, legendWidth, legendHeight)
					);
				}

				// hide the border if no items
				box[allItems.length ? 'show' : 'hide']();
			}

			// 1.x compatibility: positioning based on style
			var props = ['left', 'right', 'top', 'bottom'],
				prop,
				i = 4;
			while(i--) {
				prop = props[i];
				if (style[prop] &amp;&amp; style[prop] !== 'auto') {
					options[i &lt; 2 ? 'align' : 'verticalAlign'] = prop;
					options[i &lt; 2 ? 'x' : 'y'] = pInt(style[prop]) * (i % 2 ? -1 : 1);
				}
			}

			legendGroup.align(extend(options, {
				width: legendWidth,
				height: legendHeight
			}), true, spacingBox);

			if (!isResizing) {
				positionCheckboxes();
			}
		}


		// run legend
		renderLegend();

		// move checkboxes
		addEvent(chart, 'endResize', positionCheckboxes);

		// expose
		return {
			colorizeItem: colorizeItem,
			destroyItem: destroyItem,
			renderLegend: renderLegend
		};
	};






	/**
	 * Initialize an individual series, called internally before render time
	 */
	function initSeries(options) {
		var type = options.type || optionsChart.type || optionsChart.defaultSeriesType,
			typeClass = seriesTypes[type],
			serie,
			hasRendered = chart.hasRendered;

		// an inverted chart can't take a column series and vice versa
		if (hasRendered) {
			if (inverted &amp;&amp; type === 'column') {
				typeClass = seriesTypes.bar;
			} else if (!inverted &amp;&amp; type === 'bar') {
				typeClass = seriesTypes.column;
			}
		}

		serie = new typeClass();

		serie.init(chart, options);

		// set internal chart properties
		if (!hasRendered &amp;&amp; serie.inverted) {
			inverted = true;
		}
		if (serie.isCartesian) {
			hasCartesianSeries = serie.isCartesian;
		}

		series.push(serie);

		return serie;
	}

	/**
	 * Add a series dynamically after  time
	 *
	 * @param {Object} options The config options
	 * @param {Boolean} redraw Whether to redraw the chart after adding. Defaults to true.
	 * @param {Boolean|Object} animation Whether to apply animation, and optionally animation
	 *    configuration
	 *
	 * @return {Object} series The newly created series object
	 */
	function addSeries(options, redraw, animation) {
		var series;

		if (options) {
			setAnimation(animation, chart);
			redraw = pick(redraw, true); // defaults to true

			fireEvent(chart, 'addSeries', { options: options }, function() {
				series = initSeries(options);
				series.isDirty = true;

				chart.isDirtyLegend = true; // the series array is out of sync with the display
				if (redraw) {
					chart.redraw();
				}
			});
		}

		return series;
	}

	/**
	 * Check whether a given point is within the plot area
	 *
	 * @param {Number} x Pixel x relative to the plot area
	 * @param {Number} y Pixel y relative to the plot area
	 */
	isInsidePlot = function(x, y) {
		return x &gt;= 0 &amp;&amp;
			x &lt;= plotWidth &amp;&amp;
			y &gt;= 0 &amp;&amp;
			y &lt;= plotHeight;
	};

	/**
	 * Adjust all axes tick amounts
	 */
	function adjustTickAmounts() {
		if (optionsChart.alignTicks !== false) {
			each(axes, function(axis) {
				axis.adjustTickAmount();
			});
		}
		maxTicks = null;
	}

	/**
	 * Redraw legend, axes or series based on updated data
	 *
	 * @param {Boolean|Object} animation Whether to apply animation, and optionally animation
	 *    configuration
	 */
	function redraw(animation) {
		var redrawLegend = chart.isDirtyLegend,
			hasStackedSeries,
			isDirtyBox = chart.isDirtyBox, // todo: check if it has actually changed?
			seriesLength = series.length,
			i = seriesLength,
			clipRect = chart.clipRect,
			serie;

		setAnimation(animation, chart);

		// link stacked series
		while (i--) {
			serie = series[i];
			if (serie.isDirty &amp;&amp; serie.options.stacking) {
				hasStackedSeries = true;
				break;
			}
		}
		if (hasStackedSeries) { // mark others as dirty
			i = seriesLength;
			while (i--) {
				serie = series[i];
				if (serie.options.stacking) {
					serie.isDirty = true;
				}
			}
		}

		// handle updated data in the series
		each(series, function(serie) {
			if (serie.isDirty) { // prepare the data so axis can read it
				if (serie.options.legendType === 'point') {
					redrawLegend = true;
				}
			}
		});

		// handle added or removed series
		if (redrawLegend &amp;&amp; legend.renderLegend) { // series or pie points are added or removed
			// draw legend graphics
			legend.renderLegend();

			chart.isDirtyLegend = false;
		}

		if (hasCartesianSeries) {
			if (!isResizing) {

				// reset maxTicks
				maxTicks = null;

				// set axes scales
				each(axes, function(axis) {
					axis.leastDistance = UNDEFINED;
					axis.setScale();
				});
			}
			adjustTickAmounts();
			getMargins();

			// redraw axes
			each(axes, function(axis) {
				if (axis.isDirty || isDirtyBox) {
					axis.redraw();
					isDirtyBox = true;
				}
			});


		}

		// the plot areas size has changed
		if (isDirtyBox) {
			drawChartBox();
			placeTrackerGroup();

			// move clip rect
			if (clipRect) {
				stop(clipRect);
				clipRect.animate({ // for chart resize
					width: chart.plotSizeX,
					height: chart.plotSizeY
				});
			}

		}


		// redraw affected series
		each(series, function(serie) {
			if (serie.isDirty &amp;&amp; serie.visible &amp;&amp;
					(!serie.isCartesian || serie.xAxis)) { // issue #153
				serie.redraw();
			}
		});


		// hide tooltip and hover states
		if (tracker &amp;&amp; tracker.resetTracker) {
			tracker.resetTracker();
		}

		// fire the event
		fireEvent(chart, 'redraw'); // jQuery breaks this when calling it from addEvent. Overwrites chart.redraw
	}



	/**
	 * Dim the chart and show a loading text or symbol
	 * @param {String} str An optional text to show in the loading label instead of the default one
	 */
	function showLoading(str) {
		var loadingOptions = options.loading;

		// create the layer at the first call
		if (!loadingDiv) {
			loadingDiv = createElement(DIV, {
				className: 'highcharts-loading'
			}, extend(loadingOptions.style, {
				left: plotLeft + PX,
				top: plotTop + PX,
				width: plotWidth + PX,
				height: plotHeight + PX,
				zIndex: 10,
				display: NONE
			}), container);

			loadingSpan = createElement(
				'span',
				null,
				loadingOptions.labelStyle,
				loadingDiv
			);

		}

		// update text
		loadingSpan.innerHTML = str || options.lang.loading;

		// show it
		if (!loadingShown) {
			css(loadingDiv, { opacity: 0, display: '' });
			animate(loadingDiv, {
				opacity: loadingOptions.style.opacity
			}, {
				duration: loadingOptions.showDuration
			});
			loadingShown = true;
		}
	}
	/**
	 * Hide the loading layer
	 */
	function hideLoading() {
		animate(loadingDiv, {
			opacity: 0
		}, {
			duration: options.loading.hideDuration,
			complete: function() {
				css(loadingDiv, { display: NONE });
			}
		});
		loadingShown = false;
	}

	/**
	 * Get an axis, series or point object by id.
	 * @param id {String} The id as given in the configuration options
	 */
	function get(id) {
		var i,
			j,
			points;

		// search axes
		for (i = 0; i &lt; axes.length; i++) {
			if (axes[i].options.id === id) {
				return axes[i];
			}
		}

		// search series
		for (i = 0; i &lt; series.length; i++) {
			if (series[i].options.id === id) {
				return series[i];
			}
		}

		// search points
		for (i = 0; i &lt; series.length; i++) {
			points = series[i].points;
			for (j = 0; j &lt; points.length; j++) {
				if (points[j].id === id) {
					return points[j];
				}
			}
		}
		return null;
	}

	/**
	 * Create the Axis instances based on the config options
	 */
	function getAxes() {
		var xAxisOptions = options.xAxis || {},
			yAxisOptions = options.yAxis || {},
			optionsArray,
			axis;

		// make sure the options are arrays and add some members
		xAxisOptions = splat(xAxisOptions);
		each(xAxisOptions, function(axis, i) {
			axis.index = i;
			axis.isX = true;
		});

		yAxisOptions = splat(yAxisOptions);
		each(yAxisOptions, function(axis, i) {
			axis.index = i;
		});

		// concatenate all axis options into one array
		optionsArray = xAxisOptions.concat(yAxisOptions);

		each(optionsArray, function(axisOptions) {
			axis = new Axis(axisOptions);
		});

		adjustTickAmounts();
	}


	/**
	 * Get the currently selected points from all series
	 */
	function getSelectedPoints() {
		var points = [];
		each(series, function(serie) {
			points = points.concat( grep( serie.points, function(point) {
				return point.selected;
			}));
		});
		return points;
	}

	/**
	 * Get the currently selected series
	 */
	function getSelectedSeries() {
		return grep(series, function (serie) {
			return serie.selected;
		});
	}

	/**
	 * Zoom out to 1:1
	 */
	zoomOut = function () {
		fireEvent(chart, 'selection', { resetSelection: true }, zoom);
		chart.toolbar.remove('zoom');

	};
	/**
	 * Zoom into a given portion of the chart given by axis coordinates
	 * @param {Object} event
	 */
	zoom = function (event) {

		// add button to reset selection
		var lang = defaultOptions.lang,
			animate = chart.pointCount &lt; 100;

		if (chart.resetZoomEnabled !== false) { // hook for Stock charts etc.
			chart.toolbar.add('zoom', lang.resetZoom, lang.resetZoomTitle, zoomOut);
		}

		// if zoom is called with no arguments, reset the axes
		if (!event || event.resetSelection) {
			each(axes, function(axis) {
				if (axis.options.zoomEnabled !== false) {
					axis.setExtremes(null, null, false, animate);
				}
			});
		}

		// else, zoom in on all axes
		else {
			each(event.xAxis.concat(event.yAxis), function(axisData) {
				var axis = axisData.axis;

				// don't zoom more than maxZoom
				if (chart.tracker[axis.isXAxis ? 'zoomX' : 'zoomY']) {
					axis.setExtremes(axisData.min, axisData.max, false, animate);
				}
			});
		}

		// redraw chart
		redraw();
	};

	/**
	 * Show the title and subtitle of the chart
	 *
	 * @param titleOptions {Object} New title options
	 * @param subtitleOptions {Object} New subtitle options
	 *
	 */
	function setTitle (titleOptions, subtitleOptions) {

		chartTitleOptions = merge(options.title, titleOptions);
		chartSubtitleOptions = merge(options.subtitle, subtitleOptions);

		// add title and subtitle
		each([
			['title', titleOptions, chartTitleOptions],
			['subtitle', subtitleOptions, chartSubtitleOptions]
		], function(arr) {
			var name = arr[0],
				title = chart[name],
				titleOptions = arr[1],
				chartTitleOptions = arr[2];

			if (title &amp;&amp; titleOptions) {
				title.destroy(); // remove old
				title = null;
			}
			if (chartTitleOptions &amp;&amp; chartTitleOptions.text &amp;&amp; !title) {
				chart[name] = renderer.text(
					chartTitleOptions.text,
					0,
					0
				)
				.attr({
					align: chartTitleOptions.align,
					'class': 'highcharts-'+ name,
					zIndex: 1
				})
				.css(chartTitleOptions.style)
				.add()
				.align(chartTitleOptions, false, spacingBox);
			}
		});

	}

	/**
	 * Get chart width and height according to options and container size
	 */
	function getChartSize() {

		containerWidth = (renderToClone || renderTo).offsetWidth;
		containerHeight = (renderToClone || renderTo).offsetHeight;
		chart.chartWidth = chartWidth = optionsChart.width || containerWidth || 600;
		chart.chartHeight = chartHeight = optionsChart.height ||
			// the offsetHeight of an empty container is 0 in standard browsers, but 19 in IE7:
			(containerHeight &gt; 19 ? containerHeight : 400);
	}


	/**
	 * Get the containing element, determine the size and create the inner container
	 * div to hold the chart
	 */
	function getContainer() {
		renderTo = optionsChart.renderTo;
		containerId = PREFIX + idCounter++;

		if (isString(renderTo)) {
			renderTo = doc.getElementById(renderTo);
		}

		// remove previous chart
		renderTo.innerHTML = '';

		// If the container doesn't have an offsetWidth, it has or is a child of a node
		// that has display:none. We need to temporarily move it out to a visible
		// state to determine the size, else the legend and tooltips won't render
		// properly
		if (!renderTo.offsetWidth) {
			renderToClone = renderTo.cloneNode(0);
			css(renderToClone, {
				position: ABSOLUTE,
				top: '-9999px',
				display: ''
			});
			doc.body.appendChild(renderToClone);
		}

		// get the width and height
		getChartSize();

		// create the inner container
		chart.container = container = createElement(DIV, {
				className: 'highcharts-container' +
					(optionsChart.className ? ' '+ optionsChart.className : ''),
				id: containerId
			}, extend({
				position: RELATIVE,
				overflow: HIDDEN, // needed for context menu (avoid scrollbars) and
					// content overflow in IE
				width: chartWidth + PX,
				height: chartHeight + PX,
				textAlign: 'left'
			}, optionsChart.style),
			renderToClone || renderTo
		);

		chart.renderer = renderer =
			optionsChart.forExport ? // force SVG, used for SVG export
				new SVGRenderer(container, chartWidth, chartHeight, true) :
				new Renderer(container, chartWidth, chartHeight);

		// Issue 110 workaround:
		// In Firefox, if a div is positioned by percentage, its pixel 
position may land
		// between pixels. The container itself doesn't display this, but an 
SVG element
		// inside this container will be drawn at subpixel precision. In order
 to draw
		// sharp lines, this must be compensated for. This doesn't seem to 
work inside
		// iframes though (like in jsFiddle).
		var subPixelFix, rect;
		if (isFirefox &amp;&amp; container.getBoundingClientRect) {
			subPixelFix = function() {
				css(container, { left: 0, top: 0 });
				rect = container.getBoundingClientRect();
				css(container, {
					left: (-(rect.left - pInt(rect.left))) + PX,
					top: (-(rect.top - pInt(rect.top))) + PX
				});
			};

			// run the fix now
			subPixelFix();

			// run it on resize
			addEvent(win, 'resize', subPixelFix);

			// remove it on chart destroy
			addEvent(chart, 'destroy', function() {
				removeEvent(win, 'resize', subPixelFix);
			});
		}
	}

	/**
	 * Calculate margins by rendering axis labels in a preliminary 
position. Title,
	 * subtitle and legend have already been rendered at this stage, but 
will be
	 * moved into their final positions
	 */
	getMargins = function() {
		var legendOptions = options.legend,
			legendMargin = pick(legendOptions.margin, 10),
			legendX = legendOptions.x,
			legendY = legendOptions.y,
			align = legendOptions.align,
			verticalAlign = legendOptions.verticalAlign,
			titleOffset;

		resetMargins();

		// adjust for title and subtitle
		if ((chart.title || chart.subtitle) &amp;&amp; 
!defined(optionsMarginTop)) {
			titleOffset = mathMax(
				(chart.title &amp;&amp; !chartTitleOptions.floating &amp;&amp; 
!chartTitleOptions.verticalAlign &amp;&amp; chartTitleOptions.y) || 0,
				(chart.subtitle &amp;&amp; !chartSubtitleOptions.floating &amp;&amp;
 !chartSubtitleOptions.verticalAlign &amp;&amp; chartSubtitleOptions.y) 
|| 0
			);
			if (titleOffset) {
				plotTop = mathMax(plotTop, titleOffset + 
pick(chartTitleOptions.margin, 15) + spacingTop);
			}
		}
		// adjust for legend
		if (legendOptions.enabled &amp;&amp; !legendOptions.floating) {
			if (align === 'right') { // horizontal alignment handled first
				if (!defined(optionsMarginRight)) {
					marginRight = mathMax(
						marginRight,
						legendWidth - legendX + legendMargin + spacingRight
					);
				}
			} else if (align === 'left') {
				if (!defined(optionsMarginLeft)) {
					plotLeft = mathMax(
						plotLeft,
						legendWidth + legendX + legendMargin + spacingLeft
					);
				}

			} else if (verticalAlign === 'top') {
				if (!defined(optionsMarginTop)) {
					plotTop = mathMax(
						plotTop,
						legendHeight + legendY + legendMargin + spacingTop
					);
				}

			} else if (verticalAlign === 'bottom') {
				if (!defined(optionsMarginBottom)) {
					marginBottom = mathMax(
						marginBottom,
						legendHeight - legendY + legendMargin + spacingBottom
					);
				}
			}
		}

		// adjust for scroller
		if (chart.extraBottomMargin) {
			marginBottom += chart.extraBottomMargin;
		}
		if (chart.extraTopMargin) {
			plotTop += chart.extraTopMargin;
		}

		// pre-render axes to get labels offset width
		if (hasCartesianSeries) {
			each(axes, function(axis) {
				axis.getOffset();
			});
		}

		if (!defined(optionsMarginLeft)) {
			plotLeft += axisOffset[3];
		}
		if (!defined(optionsMarginTop)) {
			plotTop += axisOffset[0];
		}
		if (!defined(optionsMarginBottom)) {
			marginBottom += axisOffset[2];
		}
		if (!defined(optionsMarginRight)) {
			marginRight += axisOffset[1];
		}

		setChartSize();

	};

	/**
	 * Add the event handlers necessary for auto resizing
	 *
	 */
	function initReflow() {
		var reflowTimeout;
		function reflow() {
			var width = optionsChart.width || renderTo.offsetWidth,
				height = optionsChart.height || renderTo.offsetHeight;

			if (width &amp;&amp; height) { // means container is display:none
				if (width !== containerWidth || height !== containerHeight) {
					clearTimeout(reflowTimeout);
					reflowTimeout = setTimeout(function() {
						resize(width, height, false);
					}, 100);
				}
				containerWidth = width;
				containerHeight = height;
			}
		}
		addEvent(win, 'resize', reflow);
		addEvent(chart, 'destroy', function() {
			removeEvent(win, 'resize', reflow);
		});
	}

	/**
	 * Resize the chart to a given width and height
	 * @param {Number} width
	 * @param {Number} height
	 * @param {Object|Boolean} animation
	 */
	resize = function(width, height, animation) {
		var chartTitle = chart.title,
			chartSubtitle = chart.subtitle;

		isResizing += 1;

		// set the animation for the current process
		setAnimation(animation, chart);

		oldChartHeight = chartHeight;
		oldChartWidth = chartWidth;
		if (defined(width)) {
			chart.chartWidth = chartWidth = mathRound(width);
		}
		if (defined(height)) {
			chart.chartHeight = chartHeight = mathRound(height);
		}

		css(container, {
			width: chartWidth + PX,
			height: chartHeight + PX
		});
		renderer.setSize(chartWidth, chartHeight, animation);

		// update axis lengths for more correct tick intervals:
		plotWidth = chartWidth - plotLeft - marginRight;
		plotHeight = chartHeight - plotTop - marginBottom;

		// handle axes
		maxTicks = null;
		each(axes, function(axis) {
			axis.isDirty = true;
			axis.setScale();
		});

		// make sure non-cartesian series are also handled
		each(series, function(serie) {
			serie.isDirty = true;
		});

		chart.isDirtyLegend = true; // force legend redraw
		chart.isDirtyBox = true; // force redraw of plot and chart border

		getMargins();

		// move titles
		if (chartTitle) {
			chartTitle.align(null, null, spacingBox);
		}
		if (chartSubtitle) {
			chartSubtitle.align(null, null, spacingBox);
		}

		redraw(animation);


		oldChartHeight = null;
		fireEvent(chart, 'resize');

		// fire endResize and set isResizing back
		setTimeout(function() {
			fireEvent(chart, 'endResize', null, function() {
				isResizing -= 1;
			});
		}, (globalAnimation &amp;&amp; globalAnimation.duration) || 500);
	};

	/**
	 * Set the public chart properties. This is done before and after the pre-render
	 * to determine margin sizes
	 */
	setChartSize = function() {

		chart.plotLeft = plotLeft = mathRound(plotLeft);
		chart.plotTop = plotTop = mathRound(plotTop);
		chart.plotWidth = plotWidth = mathRound(chartWidth - plotLeft - marginRight);
		chart.plotHeight = plotHeight = mathRound(chartHeight - plotTop - marginBottom);

		chart.plotSizeX = inverted ? plotHeight : plotWidth;
		chart.plotSizeY = inverted ? plotWidth : plotHeight;

		spacingBox = {
			x: spacingLeft,
			y: spacingTop,
			width: chartWidth - spacingLeft - spacingRight,
			height: chartHeight - spacingTop - spacingBottom
		};

		each(axes, function(axis) {
			axis.setAxisSize();
		});
	};

	/**
	 * Initial margins before auto size margins are applied
	 */
	resetMargins = function() {
		plotTop = pick(optionsMarginTop, spacingTop);
		marginRight = pick(optionsMarginRight, spacingRight);
		marginBottom = pick(optionsMarginBottom, spacingBottom);
		plotLeft = pick(optionsMarginLeft, spacingLeft);
		axisOffset = [0, 0, 0, 0]; // top, right, bottom, left
	};

	/**
	 * Draw the borders and backgrounds for chart and plot area
	 */
	drawChartBox = function() {
		var chartBorderWidth = optionsChart.borderWidth || 0,
			chartBackgroundColor = optionsChart.backgroundColor,
			plotBackgroundColor = optionsChart.plotBackgroundColor,
			plotBackgroundImage = optionsChart.plotBackgroundImage,
			mgn,
			plotSize = {
				x: plotLeft,
				y: plotTop,
				width: plotWidth,
				height: plotHeight
			};

		// Chart area
		mgn = chartBorderWidth + (optionsChart.shadow ? 8 : 0);

		if (chartBorderWidth || chartBackgroundColor) {
			if (!chartBackground) {
				chartBackground = renderer.rect(mgn / 2, mgn / 2, chartWidth - mgn, chartHeight - mgn,
						optionsChart.borderRadius, chartBorderWidth)
					.attr({
						stroke: optionsChart.borderColor,
						'stroke-width': chartBorderWidth,
						fill: chartBackgroundColor || NONE
					})
					.add()
					.shadow(optionsChart.shadow);
			} else { // resize
				chartBackground.animate(
					chartBackground.crisp(null, null, null, chartWidth - mgn, chartHeight - mgn)
				);
			}
		}


		// Plot background
		if (plotBackgroundColor) {
			if (!plotBackground) {
				plotBackground = renderer.rect(plotLeft, plotTop, plotWidth, plotHeight, 0)
					.attr({
						fill: plotBackgroundColor
					})
					.add()
					.shadow(optionsChart.plotShadow);
			} else {
				plotBackground.animate(plotSize);
			}
		}
		if (plotBackgroundImage) {
			if (!plotBGImage) {
				plotBGImage = renderer.image(plotBackgroundImage, plotLeft, plotTop, plotWidth, plotHeight)
					.add();
			} else {
				plotBGImage.animate(plotSize);
			}
		}

		// Plot area border
		if (optionsChart.plotBorderWidth) {
			if (!plotBorder) {
				plotBorder = renderer.rect(plotLeft, plotTop, plotWidth, plotHeight, 0, optionsChart.plotBorderWidth)
					.attr({
						stroke: optionsChart.plotBorderColor,
						'stroke-width': optionsChart.plotBorderWidth,
						zIndex: 4
					})
					.add();
			} else {
				plotBorder.animate(
					plotBorder.crisp(null, plotLeft, plotTop, plotWidth, plotHeight)
				);
			}
		}

		// reset
		chart.isDirtyBox = false;
	};

	/**
	 * Render all graphics for the chart
	 */
	function render () {
		var labels = options.labels,
			credits = options.credits,
			creditsHref;

		// Title
		setTitle();


		// Legend
		legend = chart.legend = new Legend(chart);

		// Get margins by pre-rendering axes
		getMargins();
		each(axes, function(axis) {
			axis.setTickPositions(true); // update to reflect the new margins
		});
		adjustTickAmounts();
		getMargins(); // second pass to check for new labels


		// Draw the borders and backgrounds
		drawChartBox();

		// Axes
		if (hasCartesianSeries) {
			each(axes, function(axis) {
				axis.render();
			});
		}


		// The series
		if (!chart.seriesGroup) {
			chart.seriesGroup = renderer.g('series-group')
				.attr({ zIndex: 3 })
				.add();
		}
		each(series, function(serie) {
			serie.translate();
			serie.setTooltipPoints();
			serie.render();
		});


		// Labels
		if (labels.items) {
			each(labels.items, function() {
				var style = extend(labels.style, this.style),
					x = pInt(style.left) + plotLeft,
					y = pInt(style.top) + plotTop + 12;

				// delete to prevent rewriting in IE
				delete style.left;
				delete style.top;

				renderer.text(
					this.html,
					x,
					y
				)
				.attr({ zIndex: 2 })
				.css(style)
				.add();

			});
		}

		// Toolbar (don't redraw)
		if (!chart.toolbar) {
			chart.toolbar = Toolbar(chart);
		}

		// Credits
		if (credits.enabled &amp;&amp; !chart.credits) {
			creditsHref = credits.href;
			renderer.text(
				credits.text,
				0,
				0
			)
			.on('click', function() {
				if (creditsHref) {
					location.href = creditsHref;
				}
			})
			.attr({
				align: credits.position.align,
				zIndex: 8
			})
			.css(credits.style)
			.add()
			.align(credits.position);
		}

		placeTrackerGroup();

		// Set flag
		chart.hasRendered = true;

		// If the chart was rendered outside the top container, put it back in
		if (renderToClone) {
			renderTo.appendChild(container);
			discardElement(renderToClone);
			//updatePosition(container);
		}
	}

	/**
	 * Clean up memory usage
	 */
	function destroy() {
		var start = + new Date();
		var i = series.length,
			parentNode = container &amp;&amp; container.parentNode;

		// fire the chart.destoy event
		fireEvent(chart, 'destroy');

		// remove events
		removeEvent(win, 'unload', destroy);
		removeEvent(chart);

		each(axes, function(axis) {
			removeEvent(axis);
		});

		// destroy each series
		while (i--) {
			series[i].destroy();
		}

		// remove container and all SVG
		if (container) { // can break in IE when destroyed before finished loading
			container.innerHTML = '';
			removeEvent(container);
			if (parentNode) {
				parentNode.removeChild(container);
			}

			// IE6 leak
			container =	null;
		}

		// IE7 leak
		if (renderer) { // can break in IE when destroyed before finished loading
			renderer.alignedObjects = null;
		}

		// memory and CPU leak
		clearInterval(tooltipInterval);

		// clean it all up
		for (i in chart) {
			delete chart[i];
		}
		//logTime &amp;&amp; console.log('Destroyed chart in '+ (new Date() - start) +' ms');
	}
	/**
	 * Prepare for first rendering after all data are loaded
	 */
	function firstRender() {

		// VML namespaces can't be added until after complete. Listening
		// for Perini's doScroll hack is not enough.
		var ONREADYSTATECHANGE = 'onreadystatechange',
		COMPLETE = 'complete';
		// Note: in spite of JSLint's complaints, win == win.top is required
		if (!hasSVG &amp;&amp; win == win.top &amp;&amp; doc.readyState !== COMPLETE) {
				doc.attachEvent(ONREADYSTATECHANGE, function() {
				doc.detachEvent(ONREADYSTATECHANGE, firstRender);
				if (doc.readyState === COMPLETE) {
					firstRender();
				}
			});
			return;
		}

		// create the container
		getContainer();

		// Run an early event after the container and renderer are established
		fireEvent(chart, 'init');
		
		// Initialize range selector for stock charts
		if (RangeSelector &amp;&amp; options.rangeSelector.enabled) {
			chart.rangeSelector = new RangeSelector(chart);
		}

		resetMargins();
		setChartSize();

		// Initialize the series
		each(options.series || [], function(serieOptions) {
			initSeries(serieOptions);
		});

		// Set the common inversion and transformation for inverted series after initSeries
		chart.inverted = inverted = pick(inverted, options.chart.inverted);


		// get axes
		getAxes();

		// Run an event where series and axes can be added
		//fireEvent(chart, 'beforeRender');
		
		// Initialize scroller for stock charts
		if (Scroller &amp;&amp; options.navigator.enabled || options.scrollbar.enabled) {
			chart.scroller = new Scroller(chart);
		}

		chart.render = render;

		// depends on inverted and on margins being set
		chart.tracker = tracker = new MouseTracker(chart, options.tooltip);


		render();

		// run callbacks
		if (callback) {
			callback.apply(chart, [chart]);
		}
		each(chart.callbacks, function(fn) {
			fn.apply(chart, [chart]);
		});

		fireEvent(chart, 'load');

	}

	// Run chart


	// Destroy the chart and free up memory.
	addEvent(win, 'unload', destroy);

	// Set up auto resize
	if (optionsChart.reflow !== false) {
		addEvent(chart, 'load', initReflow);
	}

	// Chart event handlers
	if (chartEvents) {
		for (eventType in chartEvents) {
			addEvent(chart, eventType, chartEvents[eventType]);
		}
	}


	chart.options = options;
	chart.series = series;


	chart.xAxis = [];
	chart.yAxis = [];




	// Expose methods and variables
	chart.addSeries = addSeries;
	chart.animation = pick(optionsChart.animation, true);
	chart.Axis = Axis;
	chart.destroy = destroy;
	chart.get = get;
	chart.getSelectedPoints = getSelectedPoints;
	chart.getSelectedSeries = getSelectedSeries;
	chart.hideLoading = hideLoading;
	chart.initSeries = initSeries;
	chart.isInsidePlot = isInsidePlot;
	chart.redraw = redraw;
	chart.setSize = resize;
	chart.setTitle = setTitle;
	chart.showLoading = showLoading;
	chart.pointCount = 0;
	chart.counters = new ChartCounters();

	firstRender();


} // end Chart

// Hook for exporting module
Chart.prototype.callbacks = [];
/**
 * The Point object and prototype. Inheritable and used as base for PiePoint
 */
var Point = function() {};
Point.prototype = {

	/**
	 * Initialize the point
	 * @param {Object} series The series object containing this point
	 * @param {Object} options The data in either number, array or object format
	 */
	init: function(series, options, x) {
		var point = this,
			counters = series.chart.counters,
			defaultColors;
		point.series = series;
		point.applyOptions(options, x);
		point.pointAttr = {};

		if (series.options.colorByPoint) {
			defaultColors = series.chart.options.colors;
			if (!point.options) {
				point.options = {};
			}
			point.color = point.options.color = point.color || defaultColors[counters.color++];
			
			// loop back to zero
			counters.wrapColor(defaultColors.length);
		}

		series.chart.pointCount++;
		return point;
	},
	/**
	 * Apply the options containing the x and y data and possible some extra properties.
	 * This is called on point init or from point.update.
	 *
	 * @param {Object} options
	 */
	applyOptions: function(options, x) {
		var point = this,
			series = point.series,
			optionsType = typeof options;

		point.config = options;

		// onedimensional array input
		if (optionsType === 'number' || options === null) {
			point.y = options;
		}

		// two-dimentional array
		else if (typeof options[0] === 'number') {
			point.x = options[0];
			point.y = options[1];
		}

		// object input
		else if (optionsType === 'object' &amp;&amp; typeof options.length !== 'number') {

			// copy options directly to point
			extend(point, options);
			point.options = options;
		}

		// categorized data with name in first position
		else if (typeof options[0] === 'string') {
			point.name = options[0];
			point.y = options[1];
		}

		/*
		 * If no x is set by now, get auto incremented value. All points must have an
		 * x value, however the y value can be null to create a gap in the series
		 */

		// todo: skip this? It is only used in applyOptions, in translate it should not be used
		if (point.x === UNDEFINED) {
			point.x = x === UNDEFINED ? series.autoIncrement() : x;
		}

	},

	/**
	 * Destroy a point to clear memory. Its reference still stays in series.data.
	 */
	destroy: function() {
		var point = this,
			series = point.series,
			prop;

		series.chart.pointCount--;

		if (point === series.chart.hoverPoint) {
			point.onMouseOut();
		}
		series.chart.hoverPoints = null; // remove reference

		// remove all events
		if (point.graphic) { // removeEvent and destroyElements are performance expensive
			removeEvent(point);
			point.destroyElements();
		}

		if (point.legendItem) { // pies have legend items
			point.series.chart.legend.destroyItem(point);
		}

		for (prop in point) {
			point[prop] = null;
		}


	},

	/**
	 * Destroy SVG elements associated with the point
	 */
	destroyElements: function() {
		var point = this,
			props = ['graphic', 'tracker', 'dataLabel', 'group', 'connector'],
			prop,
			i = 5;
		while (i--) {
			prop = props[i];
			if (point[prop]) {
				point[prop] = point[prop].destroy();
			}
		}
	},

	/**
	 * Return the configuration hash needed for the data label and tooltip formatters
	 */
	getLabelConfig: function() {
		var point = this;
		return {
			x: point.category,
			y: point.y,
			series: point.series,
			point: point,
			percentage: point.percentage,
			total: point.total || point.stackTotal
		};
	},

	/**
	 * Toggle the selection status of a point
	 * @param {Boolean} selected Whether to select or unselect the point.
	 * @param {Boolean} accumulate Whether to add to the previous selection. By default,
	 *     this happens if the control key (Cmd on Mac) was pressed during clicking.
	 */
	select: function(selected, accumulate) {
		var point = this,
			series = point.series,
			chart = series.chart;

		point.selected = selected = pick(selected, !point.selected);

		//series.isDirty = true;
		point.firePointEvent(selected ? 'select' : 'unselect');
		point.setState(selected &amp;&amp; SELECT_STATE);

		// unselect all other points unless Ctrl or Cmd + click
		if (!accumulate) {
			each(chart.getSelectedPoints(), function (loopPoint) {
				if (loopPoint.selected &amp;&amp; loopPoint !== point) {
					loopPoint.selected = false;
					loopPoint.setState(NORMAL_STATE);
					loopPoint.firePointEvent('unselect');
				}
			});
		}

	},

	onMouseOver: function() {
		var point = this,
			series = point.series,
			chart = series.chart,
			tooltip = chart.tooltip,
			hoverPoint = chart.hoverPoint;

		// set normal state to previous series
		if (hoverPoint &amp;&amp; hoverPoint !== point) {
			hoverPoint.onMouseOut();
		}

		// trigger the event
		point.firePointEvent('mouseOver');

		// update the tooltip
		if (tooltip &amp;&amp; (!tooltip.shared || series.noSharedTooltip)) {
			tooltip.refresh(point);
		}

		// hover this
		point.setState(HOVER_STATE);
		chart.hoverPoint = point;
	},

	onMouseOut: function() {
		var point = this;
		point.firePointEvent('mouseOut');

		point.setState();
		point.series.chart.hoverPoint = null;
	},

	/**
	 * Extendable method for formatting each point's tooltip line
	 *
	 * @param {Boolean} useHeader Whether a common header is used for multiple series in the tooltip
	 *
	 * @return {String} A string to be concatenated in to the common tooltip text
	 */
	tooltipFormatter: function(useHeader) {
		var point = this,
			series = point.series;

		return ['<span style="">', (point.name || series.name), '</span>: ',
			(!useHeader ? ('<b>x = '+ (point.name || point.x) + ',</b> ') : ''),
			'<b>', (!useHeader ? 'y = ' : '' ), point.y, '</b>'].join('');

	},

	/**
	 * Update the point with new options (typically x/y data) and optionally redraw the series.
	 *
	 * @param {Object} options Point options as defined in the series.data array
	 * @param {Boolean} redraw Whether to redraw the chart or wait for an explicit call
	 * @param {Boolean|Object} animation Whether to apply animation, and optionally animation
	 *    configuration
	 *
	 */
	update: function(options, redraw, animation) {
		var point = this,
			series = point.series,
			dataLabel = point.dataLabel,
			graphic = point.graphic,
			i,
			data = series.data,
			dataLength = data.length,
			chart = series.chart;

		redraw = pick(redraw, true);

		// fire the event with a default handler of doing the update
		point.firePointEvent('update', { options: options }, function() {

			point.applyOptions(options);

			// update visuals
			if (isObject(options)) {
				series.getAttribs();
				if (graphic) {
					graphic.attr(point.pointAttr[series.state]);
				}
			}

			// record changes in the parallel arrays
			for (i = 0; i &lt; dataLength; i++) {
				if (data[i] === point) {
					series.xData[i] = point.x;
					series.yData[i] = point.y;
					series.options.data[i] = options;
					break;
				}
			}

			// redraw
			series.isDirty = true;
			series.isDirtyData = true;
			if (redraw) {
				chart.redraw(animation);
			}
		});
	},

	/**
	 * Remove a point and optionally redraw the series and if necessary the axes
	 * @param {Boolean} redraw Whether to redraw the chart or wait for an explicit call
	 * @param {Boolean|Object} animation Whether to apply animation, and optionally animation
	 *    configuration
	 */
	remove: function(redraw, animation) {
		var point = this,
			series = point.series,
			chart = series.chart,
			i,
			data = series.data,
			dataLength = data.length;

		setAnimation(animation, chart);
		redraw = pick(redraw, true);

		// fire the event with a default handler of removing the point
		point.firePointEvent('remove', null, function() {

			//erase(series.data, point);

			for (i = 0; i &lt; dataLength; i++) {
				if (data[i] === point) {

					// splice all the parallel arrays
					data.splice(i, 1);
					series.options.data.splice(i, 1);
					series.xData.splice(i, 1);
					series.yData.splice(i, 1);
					break;
				}
			}

			point.destroy();


			// redraw
			series.isDirty = true;
			series.isDirtyData = true;
			if (redraw) {
				chart.redraw();
			}
		});


	},

	/**
	 * Fire an event on the Point object. Must not be renamed to fireEvent, as this
	 * causes a name clash in MooTools
	 * @param {String} eventType
	 * @param {Object} eventArgs Additional event arguments
	 * @param {Function} defaultFunction Default event handler
	 */
	firePointEvent: function(eventType, eventArgs, defaultFunction) {
		var point = this,
			series = this.series,
			seriesOptions = series.options;

		// load event handlers on demand to save time on mouseover/out
		if (seriesOptions.point.events[eventType] || (
				point.options &amp;&amp; point.options.events &amp;&amp; point.options.events[eventType])) {
			this.importEvents();
		}

		// add default handler if in selection mode
		if (eventType === 'click' &amp;&amp; seriesOptions.allowPointSelect) {
			defaultFunction = function (event) {
				// Control key is for Windows, meta (= Cmd key) for Mac, Shift for Opera
				point.select(null, event.ctrlKey || event.metaKey || event.shiftKey);
			};
		}

		fireEvent(this, eventType, eventArgs, defaultFunction);
	},
	/**
	 * Import events from the series' and point's options. Only do it on
	 * demand, to save processing time on hovering.
	 */
	importEvents: function() {
		if (!this.hasImportedEvents) {
			var point = this,
				options = merge(point.series.options.point, point.options),
				events = options.events,
				eventType;

			point.events = events;

			for (eventType in events) {
				addEvent(point, eventType, events[eventType]);
			}
			this.hasImportedEvents = true;

		}
	},

	/**
	 * Set the point's state
	 * @param {String} state
	 */
	setState: function(state) {
		var point = this,
			series = point.series,
			stateOptions = series.options.states,
			markerOptions = defaultPlotOptions[series.type].marker &amp;&amp; series.options.marker,
			normalDisabled = markerOptions &amp;&amp; !markerOptions.enabled,
			markerStateOptions = markerOptions &amp;&amp; markerOptions.states[state],
			stateDisabled = markerStateOptions &amp;&amp; markerStateOptions.enabled === false,
			stateMarkerGraphic = series.stateMarkerGraphic,
			chart = series.chart,
			radius,
			pointAttr = point.pointAttr;

		state = state || NORMAL_STATE; // empty string

		if (
				// already has this state
				state === point.state ||
				// selected points don't respond to hover
				(point.selected &amp;&amp; state !== SELECT_STATE) ||
				// series' state options is disabled
				(stateOptions[state] &amp;&amp; stateOptions[state].enabled === false) ||
				// point marker's state options is disabled
				(state &amp;&amp; (stateDisabled || (normalDisabled &amp;&amp; !markerStateOptions.enabled)))

			) {
			return;
		}

		// apply hover styles to the existing point
		if (point.graphic) {
			point.graphic.attr(pointAttr[state]);
		}
		// if a graphic is not applied to each point in the normal state, create a shared
		// graphic for the hover state
		else {
			if (state) {
				if (!stateMarkerGraphic) {
					radius = markerOptions.radius;
					series.stateMarkerGraphic = stateMarkerGraphic = chart.renderer.symbol(
						series.symbol,
						- radius,
						- radius,
						2 * radius,
						2 * radius
					)
					.attr(pointAttr[state])
					.add(series.group);
				}
				
				stateMarkerGraphic.translate(
					point.plotX,
					point.plotY
				);
			}

			if (stateMarkerGraphic) {
				stateMarkerGraphic[state ? 'show' : 'hide']();
			}
		}

		point.state = state;
	}
};

/**
 * @classDescription The base function which all other series types inherit from. The data in the series is stored
 * in various arrays.
 *
 * - First, series.options.data contains all the original config options for
 * each point whether added by options or methods like series.addPoint.
 * - Next, series.data contains those values converted to points, but in case the series data length
 * exceeds the cropThreshold, or if the data is grouped, series.data doesn't contain all the points. It
 * only contains the points that have been created on demand.
 * - Then there's series.points that contains all currently visible point objects. In case of cropping,
 * the cropped-away points are not part of this array. The series.points array starts at series.cropStart
 * compared to series.data and series.options.data. If however the series data is grouped, these can't
 * be correlated one to one.
 * - series.xData and series.processedXData contain clean x values, equivalent to series.data and series.points.
 * - series.yData and series.processedYData contain clean x values, equivalent to series.data and series.points.
 *
 * @param {Object} chart
 * @param {Object} options
 */
var Series = function() {};

Series.prototype = {

	isCartesian: true,
	type: 'line',
	pointClass: Point,
	pointAttrToOptions: { // mapping between SVG attributes and the corresponding options
		stroke: 'lineColor',
		'stroke-width': 'lineWidth',
		fill: 'fillColor',
		r: 'radius'
	},
	init: function(chart, options) {
		var series = this,
			eventType,
			events,
			//pointEvent,
			index = chart.series.length;

		series.chart = chart;
		options = series.setOptions(options); // merge with plotOptions

		// set some variables
		extend(series, {
			index: index,
			options: options,
			name: options.name || 'Series '+ (index + 1),
			state: NORMAL_STATE,
			pointAttr: {},
			visible: options.visible !== false, // true by default
			selected: options.selected === true // false by default
		});

		// register event listeners
		events = options.events;
		for (eventType in events) {
			addEvent(series, eventType, events[eventType]);
		}
		if (
			(events &amp;&amp; events.click) ||
			(options.point &amp;&amp; options.point.events &amp;&amp; options.point.events.click) ||
			options.allowPointSelect
		) {
			chart.runTrackerClick = true;
		}

		series.getColor();
		series.getSymbol();


		// set the data
		series.setData(options.data, false);

	},


	/**
	 * Return an auto incremented x value based on the pointStart and pointInterval options.
	 * This is only used if an x value is not given for the point that calls autoIncrement.
	 */
	autoIncrement: function() {
		var series = this,
			options = series.options,
			xIncrement = series.xIncrement;

		xIncrement = pick(xIncrement, options.pointStart, 0);

		series.pointInterval = pick(series.pointInterval, options.pointInterval, 1);

		series.xIncrement = xIncrement + series.pointInterval;
		return xIncrement;
	},

	/**
	 * Divide the series data into segments divided by null values.
	 */
	getSegments: function() {
		var lastNull = -1,
			segments = [],
			points = this.points;

		var start = + new Date();

		// create the segments
		each(points, function(point, i) {
			if (point.y === null) {
				if (i &gt; lastNull + 1) {
					segments.push(points.slice(lastNull + 1, i));
				}
				lastNull = i;
			} else if (i === points.length - 1) { // last value
				segments.push(points.slice(lastNull + 1, i + 1));
			}
		});
		this.segments = segments;


	},
	/**
	 * Set the series options by merging from the options tree
	 * @param {Object} itemOptions
	 */
	setOptions: function(itemOptions) {
		var plotOptions = this.chart.options.plotOptions,
			data = itemOptions.data,
			options;

		itemOptions.data = null; // remove from merge to prevent looping over the data set

		options = merge(
			plotOptions[this.type],
			plotOptions.series,
			itemOptions
		);
		options.data = data;

		return options;

	},
	/**
	 * Get the series' color
	 */
	getColor: function(){
		var defaultColors = this.chart.options.colors,
			counters = this.chart.counters;
		this.color = this.options.color || defaultColors[counters.color++] || '#0000ff';
		counters.wrapColor(defaultColors.length);
	},
	/**
	 * Get the series' symbol
	 */
	getSymbol: function(){
		var defaultSymbols = this.chart.options.symbols,
			counters = this.chart.counters;
		this.symbol = this.options.marker.symbol || defaultSymbols[counters.symbol++];
		counters.wrapSymbol(defaultSymbols.length);
	},

	/**
	 * Add a point dynamically after chart load time
	 * @param {Object} options Point options as given in series.data
	 * @param {Boolean} redraw Whether to redraw the chart or wait for an explicit call
	 * @param {Boolean} shift If shift is true, a point is shifted off the start
	 *    of the series as one is appended to the end.
	 * @param {Boolean|Object} animation Whether to apply animation, and optionally animation
	 *    configuration
	 */
	addPoint: function(options, redraw, shift, animation) {
		var series = this,
			data = series.data,
			graph = series.graph,
			area = series.area,
			chart = series.chart,
			xData = series.xData,
			yData = series.yData,
			currentShift = graph.shift || 0,
			dataOptions = series.options.data,
			point;
			//point = (new series.pointClass()).init(series, options);

		setAnimation(animation, chart);

		if (graph &amp;&amp; shift) { // make graph animate sideways
			graph.shift = currentShift + 1;
		}
		if (area) {
			area.shift = currentShift + 1;
			area.isArea = true;
		}
		redraw = pick(redraw, true);


		// Get options and push the point to xData, yData and series.options. In series.generatePoints
		// the Point instance will be created on demand and pushed to the series.data array.
		point = { series: series };
		series.pointClass.prototype.applyOptions.apply(point, [options]);
		xData.push(point.x);
		yData.push(point.y);
		dataOptions.push(options);


		// Shift the first point off the parallel arrays
		// todo: consider series.removePoint(i) method
		if (shift) {
			if (data[0]) {
				data[0].remove(false);
			} else {
				data.shift();
				xData.shift();
				yData.shift();
				dataOptions.shift();
			}
		}
		series.getAttribs();

		// redraw
		series.isDirty = true;
		series.isDirtyData = true;
		if (redraw) {
			chart.redraw();
		}
	},

	/**
	 * Replace the series data with a new set of data
	 * @param {Object} data
	 * @param {Object} redraw
	 */
	setData: function(data, redraw) {
		var series = this,
			oldData = series.points,
			options = series.options,
			initialColor = series.initialColor,
			chart = series.chart,
			i;

		series.xIncrement = null; // reset for new data
		if (defined(initialColor)) { // reset colors for pie
			chart.counters.color = initialColor;
		}

		// parallel arrays
		var xData = [],
			yData = [],
			dataLength = data.length,
			turboThreshold = options.turboThreshold || 1000,
			pt;

		// In turbo mode, only one- or twodimensional arrays of numbers are allowed. The
		// first value is tested, and we assume that all the rest are defined the same
		// way. Although the 'for' loops are similar, they are repeated inside each
		// if-else conditional for max performance.
		if (dataLength &gt; turboThreshold) {
			if (isNumber(data[0])) { // assume all points are numbers
				var x = pick(options.pointStart, 0),
					pointInterval = pick(options.pointInterval, 1);

				for (i = 0; i &lt; dataLength; i++) {
					xData[i] = x;
					yData[i] = data[i];
					x += pointInterval;
				}
				series.xIncrement = x;
			} else if (data[0].constructor === Array) { // assume all points are arrays
				if (series.valueCount === 4) { // [x, o, h, l, c]
					for (i = 0; i &lt; dataLength; i++) {
						pt = data[i];
						xData[i] = pt[0];
						yData[i] = pt.slice(1, 5);
					}
				} else { // [x, y]
					for (i = 0; i &lt; dataLength; i++) {
						pt = data[i];
						xData[i] = pt[0];
						yData[i] = pt[1];
					}
				}
			}
		} else {
			for (i = 0; i &lt; dataLength; i++) {
				pt = { series: series };
				series.pointClass.prototype.applyOptions.apply(pt, [data[i]]);
				xData[i] = pt.x;
				yData[i] = pt.y;
			}
		}

		series.data = null;
		series.options.data = data;
		series.xData = xData;
		series.yData = yData;


		// destroy old points
		i = (oldData &amp;&amp; oldData.length) || 0;
		while (i--) {
			oldData[i].destroy();
		}

		// redraw
		series.isDirty = series.isDirtyData = chart.isDirtyBox = true;
		if (pick(redraw, true)) {
			chart.redraw(false);
		}
	},

	/**
	 * Remove a series and optionally redraw the chart
	 *
	 * @param {Boolean} redraw Whether to redraw the chart or wait for an explicit call
	 * @param {Boolean|Object} animation Whether to apply animation, and optionally animation
	 *    configuration
	 */

	remove: function(redraw, animation) {
		var series = this,
			chart = series.chart;
		redraw = pick(redraw, true);

		if (!series.isRemoving) {  /* prevent triggering native event in jQuery
				(calling the remove function from the remove event) */
			series.isRemoving = true;

			// fire the event with a default handler of removing the point
			fireEvent(series, 'remove', null, function() {


				// destroy elements
				series.destroy();


				// redraw
				chart.isDirtyLegend = chart.isDirtyBox = true;
				if (redraw) {
					chart.redraw(animation);
				}
			});

		}
		series.isRemoving = false;
	},

	/**
	 * Process the data by cropping away unused data points if the series is longer
	 * than the crop threshold. This saves computing time for lage series.
	 */
	processData: function() {
		var series = this,
			processedXData = series.xData, // copied during slice operation below
			processedYData = series.yData,
			dataLength = processedXData.length,
			cropStart = 0,
			i, // loop variable
			cropThreshold = series.options.cropThreshold; // todo: consider combining it with turboThreshold


		// optionally filter out points outside the plot area
		if (!cropThreshold || dataLength &gt; cropThreshold) {
			var extremes = series.xAxis.getExtremes(),
				min = extremes.min,
				max = extremes.max,
				cropEnd = dataLength - 1,
				point;

			// only crop if it's actually spilling out
			if (processedXData[0] &lt; min || processedXData[cropEnd] &gt; max) {

				// iterate up to find slice start
				for (i = 0; i &lt; dataLength; i++) {
					if (processedXData[i] &gt;= min) {
						cropStart = mathMax(0, i - 1);
						break;
					}
				}
				// proceed to find slice end
				for (i; i &lt; dataLength; i++) {
					if (processedXData[i] &gt; max) {
						cropEnd = i + 1;
						break;
					}
				}
				processedXData = processedXData.slice(cropStart, cropEnd);
				processedYData = processedYData.slice(cropStart, cropEnd);
			}
		}

		series.cropStart = cropStart;
		series.processedXData = processedXData;
		series.processedYData = processedYData;
	},

	/**
	 * Generate the data point after the data has been processed by cropping away
	 * unused points and optionally grouped in Highcharts Stock.
	 */
	generatePoints: function() {
		var series = this,
			options = series.options,
			dataOptions = options.data,
			hasProcessedData = series.prosessedXData !== series.xData,
			data = series.data,
			dataLength,
			processedXData = series.processedXData,
			processedYData = series.processedYData,
			pointClass = series.pointClass,
			processedDataLength = processedXData.length,
			cropStart = series.cropStart || 0,
			cursor,
			hasGroupedData = series.hasGroupedData,
			point,
			points = [],
			i;

		if (!data &amp;&amp; !hasGroupedData) {
			var arr = [];
			arr.length = dataOptions.length;
			data = series.data = arr;
		}

		for (i = 0; i &lt; processedDataLength; i++) {
			cursor = cropStart + i;
			if (!hasGroupedData) {
				if (data[cursor]) {
					point = data[cursor];
				} else {
					data[cursor] = point = (new pointClass()).init(series, dataOptions[cursor], processedXData[i]);
				}
				points[i] = point;
			} else {
				// splat the y data in case of ohlc data array
				points[i] = (new pointClass()).init(series, [processedXData[i]].concat(splat(processedYData[i])));
			}
		}

		// hide cropped-away points - this only runs when the number of points is above cropThreshold
		if (data &amp;&amp; processedDataLength !== (dataLength = data.length)) {
			for (i = 0; i &lt; dataLength; i++) {
				if (i === cropStart &amp;&amp; !hasGroupedData) { // when has grouped data, clear all points
					i += processedDataLength;
				}
				if (data[i]) {
					data[i].destroyElements();
				}
			}
		}

		series.data = data;
		series.points = points;
	},

	/**
	 * Translate data points from raw data values to chart specific positioning data
	 * needed later in drawPoints, drawGraph and drawTracker.
	 */
	translate: function() {
		if (!this.processedXData) { // hidden series
			this.processData();
		}
		this.generatePoints();
		var series = this,
			chart = series.chart,
			options = series.options,
			stacking = options.stacking,
			xAxis = series.xAxis,
			categories = xAxis.categories,
			yAxis = series.yAxis,
			points = series.points,
			//data = series.data,
			//dataLength = data.length,
			//point,
			//xData = series.processedXData || series.xData,
			//yData = series.processedYData || series.yData,
			dataLength = points.length,
			//closestPoints,
			//smallestInterval,
			leastDistance = xAxis.leastDistance,
			interval,
			i,
			cropI = -1;


		for (i = 0; i &lt; dataLength; i++) {
			var point = points[i],
				xValue = point.x,
				yValue = point.y,
				yBottom = point.low,
				stack = yAxis.stacks[(yValue &lt; 0 ? '-' : '') + series.stackKey],
				pointStack,
				distance,
				pointStackTotal;
			// get the plotX translation
			point.plotX = series.xAxis.translate(xValue);

			// calculate the bottom y value for stacked series
			if (stacking &amp;&amp; series.visible &amp;&amp; stack &amp;&amp; stack[xValue]) {
				pointStack = stack[xValue];
				pointStackTotal = pointStack.total;
				pointStack.cum = yBottom = pointStack.cum - yValue; // start from top
				yValue = yBottom + yValue;

				if (stacking === 'percent') {
					yBottom = pointStackTotal ? yBottom * 100 / pointStackTotal : 0;
					yValue = pointStackTotal ? yValue * 100 / pointStackTotal : 0;
				}

				point.percentage = pointStackTotal ? point.y * 100 / pointStackTotal : 0;
				point.stackTotal = pointStackTotal;
			}

			if (defined(yBottom)) {
				point.yBottom = yAxis.translate(yBottom, 0, 1, 0, 1);
			}

			// set the y value
			if (yValue !== null) {
				point.plotY = yAxis.translate(yValue, 0, 1, 0, 1);
			}

			// set client related positions for mouse tracking
			point.clientX = chart.inverted ?
				chart.plotHeight - point.plotX :
				point.plotX; // for mouse tracking

			// some API data
			point.category = categories &amp;&amp; categories[point.x] !== UNDEFINED ?
				categories[point.x] : point.x;

			// get the smallest distance between points for columns
			if (series.getDistance &amp;&amp; i) {
				distance = mathAbs(point.plotX - points[i - 1].plotX);
				leastDistance = leastDistance === UNDEFINED ? distance : mathMin(distance, leastDistance);
			}

		}

		xAxis.leastDistance = leastDistance;

		// now that we have the cropped data, build the segments
		series.getSegments();
	},
	/**
	 * Memoize tooltip texts and positions
	 */
	setTooltipPoints: function (renew) {
		var series = this,
			chart = series.chart,
			inverted = chart.inverted,
			points = [],
			pointsLength,
			plotSize = mathRound((inverted ? chart.plotTop : chart.plotLeft) + chart.plotSizeX),
			low,
			high,
			xAxis = series.xAxis,
			point,
			i,
			tooltipPoints = []; // a lookup array for each pixel in the x dimension

		// renew
		if (renew) {
			series.tooltipPoints = null;
		}

		// concat segments to overcome null values
		each(series.segments || series.points, function(segment){
			points = points.concat(segment);
		});

		// loop the concatenated points and apply each point to all the closest
		// pixel positions
		if (xAxis &amp;&amp; xAxis.reversed) {
			points = points.reverse();//reverseArray(points);
		}

		//each(points, function(point, i) {
		pointsLength = points.length;
		for (i = 0; i &lt; pointsLength; i++) {
			point = points[i];
			low = points[i - 1] ? points[i - 1]._high + 1 : 0;
			high = point._high = points[i + 1] ? (
				mathFloor((point.plotX + (points[i + 1] ?
					points[i + 1].plotX : plotSize)) / 2)) :
					plotSize;

			while (low &lt;= high) {
				tooltipPoints[inverted ? plotSize - low++ : low++] = point;
			}
		}
		series.tooltipPoints = tooltipPoints;
	},




	/**
	 * Series mouse over handler
	 */
	onMouseOver: function() {
		var series = this,
			chart = series.chart,
			hoverSeries = chart.hoverSeries;

		if (!hasTouch &amp;&amp; chart.mouseIsDown) {
			return;
		}

		// set normal state to previous series
		if (hoverSeries &amp;&amp; hoverSeries !== series) {
			hoverSeries.onMouseOut();
		}

		// trigger the event, but to save processing time,
		// only if defined
		if (series.options.events.mouseOver) {
			fireEvent(series, 'mouseOver');
		}

		// hover this
		series.setState(HOVER_STATE);
		chart.hoverSeries = series;
	},

	/**
	 * Series mouse out handler
	 */
	onMouseOut: function() {
		// trigger the event only if listeners exist
		var series = this,
			options = series.options,
			chart = series.chart,
			tooltip = chart.tooltip,
			hoverPoint = chart.hoverPoint;

		// trigger mouse out on the point, which must be in this series
		if (hoverPoint) {
			hoverPoint.onMouseOut();
		}

		// fire the mouse out event
		if (series &amp;&amp; options.events.mouseOut) {
			fireEvent(series, 'mouseOut');
		}


		// hide the tooltip
		if (tooltip &amp;&amp; !options.stickyTracking) {
			tooltip.hide();
		}

		// set normal state
		series.setState();
		chart.hoverSeries = null;
	},

	/**
	 * Animate in the series
	 */
	animate: function(init) {
		var series = this,
			chart = series.chart,
			clipRect = series.clipRect,
			animation = series.options.animation;

		if (animation &amp;&amp; !isObject(animation)) {
			animation = {};
		}

		if (init) { // initialize the animation
			if (!clipRect.isAnimating) { // apply it only for one of the series
				clipRect.attr( 'width', 0 );
				clipRect.isAnimating = true;
			}

		} else { // run the animation
			clipRect.animate({
				width: chart.plotSizeX
			}, animation);

			// delete this function to allow it only once
			this.animate = null;
		}
	},


	/**
	 * Draw the markers
	 */
	drawPoints: function(){
		var series = this,
			pointAttr,
			points = series.points,
			chart = series.chart,
			plotX,
			plotY,
			i,
			point,
			radius,
			graphic;

		if (series.options.marker.enabled) {
			i = points.length;
			while (i--) {
				point = points[i];
				plotX = point.plotX;
				plotY = point.plotY;
				graphic = point.graphic;

				// only draw the point if y is defined
				if (plotY !== UNDEFINED &amp;&amp; !isNaN(plotY)) {

					// shortcuts
					pointAttr = point.pointAttr[point.selected ? SELECT_STATE : NORMAL_STATE];
					radius = pointAttr.r;

					if (graphic) { // update
						graphic.animate({
							x: plotX - radius,
							y: plotY - radius,
							width: 2 * radius,
							height: 2 * radius
						});
					} else {
						point.graphic = chart.renderer.symbol(
							pick(point.marker &amp;&amp; point.marker.symbol, series.symbol),
							plotX - radius,
							plotY - radius,
							2 * radius,
							2 * radius
						)
						.attr(pointAttr)
						.add(series.group);
					}
				}
			}
		}

	},

	/**
	 * Convert state properties from API naming conventions to SVG attributes
	 *
	 * @param {Object} options API options object
	 * @param {Object} base1 SVG attribute object to inherit from
	 * @param {Object} base2 Second level SVG attribute object to inherit from
	 */
	convertAttribs: function(options, base1, base2, base3) {
		var conversion = this.pointAttrToOptions,
			attr,
			option,
			obj = {};

		options = options || {};
		base1 = base1 || {};
		base2 = base2 || {};
		base3 = base3 || {};

		for (attr in conversion) {
			option = conversion[attr];
			obj[attr] = pick(options[option], base1[attr], base2[attr], base3[attr]);
		}
		return obj;
	},

	/**
	 * Get the state attributes. Each series type has its own set of attributes
	 * that are allowed to change on a point's state change. Series wide attributes are stored for
	 * all series, and additionally point specific attributes are stored for all
	 * points with individual marker options. If such options are not defined for the point,
	 * a reference to the series wide attributes is stored in point.pointAttr.
	 */
	getAttribs: function() {
		var series = this,
			normalOptions = defaultPlotOptions[series.type].marker ? series.options.marker : series.options,
			stateOptions = normalOptions.states,
			stateOptionsHover = stateOptions[HOVER_STATE],
			pointStateOptionsHover,
			seriesColor = series.color,
			normalDefaults = {
				stroke: seriesColor,
				fill: seriesColor
			},
			points = series.points,
			i,
			point,
			seriesPointAttr = [],
			pointAttr,
			pointAttrToOptions = series.pointAttrToOptions,
			hasPointSpecificOptions,
			key;

		// series type specific modifications
		if (series.options.marker) { // line, spline, area, areaspline, scatter

			// if no hover radius is given, default to normal radius + 2
			stateOptionsHover.radius = stateOptionsHover.radius || normalOptions.radius + 2;
			stateOptionsHover.lineWidth = stateOptionsHover.lineWidth || normalOptions.lineWidth + 1;

		} else { // column, bar, pie

			// if no hover color is given, brighten the normal color
			stateOptionsHover.color = stateOptionsHover.color ||
				Color(stateOptionsHover.color || seriesColor)
					.brighten(stateOptionsHover.brightness).get();
		}

		// general point attributes for the series normal state
		seriesPointAttr[NORMAL_STATE] = series.convertAttribs(normalOptions, normalDefaults);

		// HOVER_STATE and SELECT_STATE states inherit from normal state except the default radius
		each([HOVER_STATE, SELECT_STATE], function(state) {
			seriesPointAttr[state] =
					series.convertAttribs(stateOptions[state], seriesPointAttr[NORMAL_STATE]);
		});

		// set it
		series.pointAttr = seriesPointAttr;


		// Generate the point-specific attribute collections if specific point
		// options are given. If not, create a referance to the series wide point
		// attributes
		i = points.length;
		while (i--) {
			point = points[i];
			normalOptions = (point.options &amp;&amp; point.options.marker) || point.options;
			if (normalOptions &amp;&amp; normalOptions.enabled === false) {
				normalOptions.radius = 0;
			}
			hasPointSpecificOptions = false;

			// check if the point has specific visual options
			if (point.options) {
				for (key in pointAttrToOptions) {
					if (defined(normalOptions[pointAttrToOptions[key]])) {
						hasPointSpecificOptions = true;
					}
				}
			}



			// a specific marker config object is defined for the individual point:
			// create it's own attribute collection
			if (hasPointSpecificOptions) {

				pointAttr = [];
				stateOptions = normalOptions.states || {}; // reassign for individual point
				pointStateOptionsHover = stateOptions[HOVER_STATE] = stateOptions[HOVER_STATE] || {};

				// if no hover color is given, brighten the normal color
				if (!series.options.marker) { // column, bar, point
					pointStateOptionsHover.color =
						Color(pointStateOptionsHover.color || point.options.color)
							.brighten(pointStateOptionsHover.brightness ||
								stateOptionsHover.brightness).get();

				}

				// normal point state inherits series wide normal state
				pointAttr[NORMAL_STATE] = series.convertAttribs(normalOptions, seriesPointAttr[NORMAL_STATE]);

				// inherit from point normal and series hover
				pointAttr[HOVER_STATE] = series.convertAttribs(
					stateOptions[HOVER_STATE],
					seriesPointAttr[HOVER_STATE],
					pointAttr[NORMAL_STATE]
				);
				// inherit from point normal and series hover
				pointAttr[SELECT_STATE] = series.convertAttribs(
					stateOptions[SELECT_STATE],
					seriesPointAttr[SELECT_STATE],
					pointAttr[NORMAL_STATE]
				);



			// no marker config object is created: copy a reference to the series-wide
			// attribute collection
			} else {
				pointAttr = seriesPointAttr;
			}

			point.pointAttr = pointAttr;

		}

	},


	/**
	 * Clear DOM objects and free up memory
	 */
	destroy: function() {
		var series = this,
			chart = series.chart,
			//chartSeries = series.chart.series,
			clipRect = series.clipRect,
			issue134 = /\/5[0-9\.]+ (Safari|Mobile)\//.test(userAgent), // todo: update when Safari bug is fixed
			destroy,
			i,
			data = series.data || [],
			point,
			prop;

		// remove all events
		removeEvent(series);

		// remove legend items
		if (series.legendItem) {
			series.chart.legend.destroyItem(series);
		}

		// destroy all points with their elements
		i = data.length;
		while(i--) {
			point = data[i];
			if (point &amp;&amp; point.destroy) {
				point.destroy();
			}
		}
		series.points = null;

		// destroy all SVGElements associated to the series
		each(['area', 'graph', 'dataLabelsGroup', 'group', 'tracker'], function(prop) {
			if (series[prop]) {

				// issue 134 workaround
				destroy = issue134 &amp;&amp; prop === 'group' ?
					'hide' :
					'destroy';

				series[prop][destroy]();
			}
		});

		// remove from hoverSeries
		if (chart.hoverSeries === series) {
			chart.hoverSeries = null;
		}
		erase(chart.series, series);

		// clear all members
		for (prop in series) {
			delete series[prop];
		}
	},

	/**
	 * Draw the data labels
	 */
	drawDataLabels: function() {
		if (this.options.dataLabels.enabled) {
			var series = this,
				x,
				y,
				points = series.points,
				options = series.options.dataLabels,
				str,
				dataLabelsGroup = series.dataLabelsGroup,
				chart = series.chart,
				inverted = chart.inverted,
				seriesType = series.type,
				color,
				stacking = series.options.stacking,
				isBarLike = seriesType === 'column' || seriesType === 'bar',
				vAlignIsNull = options.verticalAlign === null,
				yIsNull = options.y === null;

			if (isBarLike) {
				if (stacking) {
					// In stacked series the default label placement is inside the bars
					if (vAlignIsNull) {
						options = merge(options, {verticalAlign: 'middle'});
					}

					// If no y delta is specified, try to create a good default
					if (yIsNull) {
						options = merge(options, {y: {top: 14, middle: 4, bottom: -6}[options.verticalAlign]});
					}
				} else {
					// In non stacked series the default label placement is on top of the bars
					if (vAlignIsNull) {
						options = merge(options, {verticalAlign: 'top'});
					}

					// If no y delta is specified, set the default
					if (yIsNull) {
						options = merge(options, {y: -6});
					}
				}
			}


			// create a separate group for the data labels to avoid rotation
			if (!dataLabelsGroup) {
				dataLabelsGroup = series.dataLabelsGroup =
					chart.renderer.g('data-labels')
						.attr({
							visibility: series.visible ? VISIBLE : HIDDEN,
							zIndex: 6
						})
						.translate(chart.plotLeft, chart.plotTop)
						.add();
			}

			// determine the color
			color = options.color;
			if (color === 'auto') { // 1.0 backwards compatibility
				color = null;
			}
			options.style.color = pick(color, series.color);

			// make the labels for each point
			each(points, function(point, i){
				var barX = point.barX,
					plotX = (barX &amp;&amp; barX + point.barW / 2) || point.plotX || -999,
					plotY = pick(point.plotY, -999),
					dataLabel = point.dataLabel,
					align = options.align,
					individualYDelta = yIsNull ? (point.y &gt; 0 ? -6 : 12) : options.y;

				// get the string
				str = options.formatter.call(point.getLabelConfig());
				x = (inverted ? chart.plotWidth - plotY : plotX) + options.x;
				y = (inverted ? chart.plotHeight - plotX : plotY) + individualYDelta;
				
				// in columns, align the string to the column
				if (seriesType === 'column') {
					x += { left: -1, right: 1 }[align] * point.barW / 2 || 0;
				}


				// update existing label
				if (dataLabel) {
					// vertically centered
					if (inverted &amp;&amp; !options.y) {
						y = y + pInt(dataLabel.styles.lineHeight) * 0.9 - dataLabel.getBBox().height / 2;
					}
					dataLabel
						.attr({
							text: str
						}).animate({
							x: x,
							y: y
						});
				// create new label
				} else if (defined(str)) {
					dataLabel = point.dataLabel = chart.renderer.text(
						str,
						x,
						y
					)
					.attr({
						align: align,
						rotation: options.rotation,
						zIndex: 1
					})
					.css(options.style)
					.add(dataLabelsGroup);
					// vertically centered
					if (inverted &amp;&amp; !options.y) {
						dataLabel.attr({
							y: y + pInt(dataLabel.styles.lineHeight) * 0.9 - dataLabel.getBBox().height / 2
						});
					}
				}

				if (isBarLike &amp;&amp; series.options.stacking) {
					var barY = point.barY,
						barW = point.barW,
						barH = point.barH;

					dataLabel.align(options, null,
						{
							x: inverted ? chart.plotWidth - barY - barH : barX,
							y: inverted ? chart.plotHeight - barX - barW : barY,
							width: inverted ? barH : barW,
							height: inverted ? barW : barH
						});
				}
			});
		}
	},

	/**
	 * Draw the actual graph
	 */
	drawGraph: function(state) {
		var series = this,
			options = series.options,
			chart = series.chart,
			plotLeft = chart.plotLeft,
			plotRight = plotLeft + chart.plotWidth,
			graph = series.graph,
			graphPath = [],
			fillColor,
			area = series.area,
			group = series.group,
			color = options.lineColor || series.color,
			lineWidth = options.lineWidth,
			dashStyle =  options.dashStyle,
			segmentPath,
			renderer = chart.renderer,
			translatedThreshold = series.yAxis.getThreshold(options.threshold),
			useArea = /^area/.test(series.type),
			singlePoints = [], // used in drawTracker
			areaPath = [],
			attribs;


		// divide into segments and build graph and area paths
		each(series.segments, function(segment) {
			segmentPath = [];

			// build the segment line
			each(segment, function(point, i) {

				if (series.getPointSpline) { // generate the spline as defined in the SplineSeries object
					segmentPath.push.apply(segmentPath, series.getPointSpline(segment, point, i));

				} else {

					// moveTo or lineTo
					segmentPath.push(i ? L : M);

					// step line?
					if (i &amp;&amp; options.step) {
						var lastPoint = segment[i - 1];
						segmentPath.push(
							point.plotX,
							lastPoint.plotY
						);
					}

					// normal line to next point
					segmentPath.push(
						point.plotX,
						point.plotY
					);
				}
			});

			// add the segment to the graph, or a single point for tracking
			if (segment.length &gt; 1) {
				graphPath = graphPath.concat(segmentPath);
			} else {
				singlePoints.push(segment[0]);
			}

			// build the area
			if (useArea) {
				var areaSegmentPath = [],
					i,
					segLength = segmentPath.length;
				for (i = 0; i &lt; segLength; i++) {
					areaSegmentPath.push(segmentPath[i]);
				}
				if (segLength === 3) { // for animation from 1 to two points
					areaSegmentPath.push(L, segmentPath[1], segmentPath[2]);
				}
				if (options.stacking &amp;&amp; series.type !== 'areaspline') {
					// follow stack back. Todo: implement areaspline
					for (i = segment.length - 1; i &gt;= 0; i--) {
						areaSegmentPath.push(segment[i].plotX, segment[i].yBottom);
					}

				} else { // follow zero line back
					areaSegmentPath.push(
						L,
						segment[segment.length - 1].plotX,
						translatedThreshold,
						L,
						segment[0].plotX,
						translatedThreshold
					);
				}
				areaPath = areaPath.concat(areaSegmentPath);
			}
		});

		// used in drawTracker:
		series.graphPath = graphPath;
		series.singlePoints = singlePoints;

		// draw the area if area series or areaspline
		if (useArea) {
			fillColor = pick(
				options.fillColor,
				Color(series.color).setOpacity(options.fillOpacity || 0.75).get()
			);
			if (area) {
				area.animate({ d: areaPath });

			} else {
				// draw the area
				series.area = series.chart.renderer.path(areaPath)
					.attr({
						fill: fillColor
					}).add(group);
			}
		}

		// draw the graph
		if (graph) {
			//graph.animate({ d: graphPath.join(' ') });
			graph.animate({ d: graphPath });

		} else {
			if (lineWidth) {
				attribs = {
					'stroke': color,
					'stroke-width': lineWidth
				};
				if (dashStyle) {
					attribs.dashstyle = dashStyle;
				}

				series.graph = renderer.path(graphPath)
					.attr(attribs).add(group).shadow(options.shadow);
			}
		}
	},


	/**
	 * Render the graph and markers
	 */
	render: function() {
		var series = this,
			chart = series.chart,
			group,
			setInvert,
			options = series.options,
			doClip = options.clip !== false,
			animation = options.animation,
			doAnimation = animation &amp;&amp; series.animate,
			duration = doAnimation ? (animation &amp;&amp; animation.duration) || 500 : 0,
			clipRect = series.clipRect,
			renderer = chart.renderer;


		// Add plot area clipping rectangle. If this is before chart.hasRendered,
		// create one shared clipRect.

		// Todo: since creating the clip property, the clipRect is created but
		// never used when clip is false. A better way would be that the animation
		// would run, then the clipRect destroyed.
		if (!clipRect) {
			clipRect = series.clipRect = !chart.hasRendered &amp;&amp; chart.clipRect ?
				chart.clipRect :
				renderer.clipRect(0, 0, chart.plotSizeX, chart.plotSizeY);
			if (!chart.clipRect) {
				chart.clipRect = clipRect;
			}
		}


		// the group
		if (!series.group) {
			group = series.group = renderer.g('series');

			if (chart.inverted) {
				setInvert = function() {
					group.attr({
						width: chart.plotWidth,
						height: chart.plotHeight
					}).invert();
				};

				setInvert(); // do it now
				addEvent(chart, 'resize', setInvert); // do it on resize
			}

			if (doClip) {
				group.clip(series.clipRect);
			}
			group.attr({
					visibility: series.visible ? VISIBLE : HIDDEN,
					zIndex: options.zIndex
				})
				.translate(series.xAxis.left, series.yAxis.top)
				.add(chart.seriesGroup);
		}

		series.drawDataLabels();

		// initiate the animation
		if (doAnimation) {
			series.animate(true);
		}

		// cache attributes for shapes
		series.getAttribs();

		// draw the graph if any
		if (series.drawGraph) {
			series.drawGraph();
		}

		// draw the points
		series.drawPoints();

		// draw the mouse tracking area
		if (series.options.enableMouseTracking !== false) {
			series.drawTracker();
		}

		// run the animation
		if (doAnimation) {
			series.animate();
		}

		// finish the individual clipRect
		setTimeout(function() {
			clipRect.isAnimating = false;
			group = series.group; // can be destroyed during the timeout
			if (group &amp;&amp; clipRect !== chart.clipRect &amp;&amp; clipRect.renderer) {
				if (doClip) {
					group.clip((series.clipRect = chart.clipRect));
				}
				clipRect.destroy();
			}
		}, duration);


		series.isDirty = series.isDirtyData = false; // means data is in accordance with what you see

	},

	/**
	 * Redraw the series after an update in the axes.
	 */
	redraw: function() {
		var series = this,
			chart = series.chart,
			clipRect = series.clipRect,
			group = series.group;

		// reposition on resize
		if (group) {
			if (chart.inverted) {
				group.attr({
					width: chart.plotWidth,
					height: chart.plotHeight
				});
			}

			group.animate({
				translateX: series.xAxis.left,
				translateY: series.yAxis.top
			});
		}

		series.translate();
		series.setTooltipPoints(true);

		if (series.isDirtyData) {
			fireEvent(series, 'updatedData');
		}
		series.render();
	},

	/**
	 * Set the state of the graph
	 */
	setState: function(state) {
		var series = this,
			options = series.options,
			graph = series.graph,
			stateOptions = options.states,
			lineWidth = options.lineWidth;

		state = state || NORMAL_STATE;

		if (series.state !== state) {
			series.state = state;

			if (stateOptions[state] &amp;&amp; stateOptions[state].enabled === false) {
				return;
			}

			if (state) {
				lineWidth = stateOptions[state].lineWidth || lineWidth + 1;
			}

			if (graph &amp;&amp; !graph.dashstyle) { // hover is turned off for dashed lines in VML
				graph.attr({ // use attr because animate will cause any other animation on the graph to stop
					'stroke-width': lineWidth
				}, state ? 0 : 500);
			}
		}
	},

	/**
	 * Set the visibility of the graph
	 *
	 * @param vis {Boolean} True to show the series, false to hide. If UNDEFINED,
	 *        the visibility is toggled.
	 */
	setVisible: function(vis, redraw) {
		var series = this,
			chart = series.chart,
			legendItem = series.legendItem,
			seriesGroup = series.group,
			seriesTracker = series.tracker,
			dataLabelsGroup = series.dataLabelsGroup,
			showOrHide,
			i,
			points = series.points,
			point,
			ignoreHiddenSeries = chart.options.chart.ignoreHiddenSeries,
			oldVisibility = series.visible;

		// if called without an argument, toggle visibility
		series.visible = vis = vis === UNDEFINED ? !oldVisibility : vis;
		showOrHide = vis ? 'show' : 'hide';

		// show or hide series
		if (seriesGroup) { // pies don't have one
			seriesGroup[showOrHide]();
		}

		// show or hide trackers
		if (seriesTracker) {
			seriesTracker[showOrHide]();
		} else {
			i = points.length;
			while (i--) {
				point = points[i];
				if (point.tracker) {
					point.tracker[showOrHide]();
				}
			}
		}


		if (dataLabelsGroup) {
			dataLabelsGroup[showOrHide]();
		}

		if (legendItem) {
			chart.legend.colorizeItem(series, vis);
		}


		// rescale or adapt to resized chart
		series.isDirty = true;
		// in a stack, all other series are affected
		if (series.options.stacking) {
			each(chart.series, function(otherSeries) {
				if (otherSeries.options.stacking &amp;&amp; otherSeries.visible) {
					otherSeries.isDirty = true;
				}
			});
		}

		if (ignoreHiddenSeries) {
			chart.isDirtyBox = true;
		}
		if (redraw !== false) {
			chart.redraw();
		}

		fireEvent(series, showOrHide);
	},

	/**
	 * Show the graph
	 */
	show: function() {
		this.setVisible(true);
	},

	/**
	 * Hide the graph
	 */
	hide: function() {
		this.setVisible(false);
	},


	/**
	 * Set the selected state of the graph
	 *
	 * @param selected {Boolean} True to select the series, false to unselect. If
	 *        UNDEFINED, the selection state is toggled.
	 */
	select: function(selected) {
		var series = this;
		// if called without an argument, toggle
		series.selected = selected = (selected === UNDEFINED) ? !series.selected : selected;

		if (series.checkbox) {
			series.checkbox.checked = selected;
		}

		fireEvent(series, selected ? 'select' : 'unselect');
	},


	/**
	 * Draw the tracker object that sits above all data labels and markers to
	 * track mouse events on the graph or points. For the line type charts
	 * the tracker uses the same graphPath, but with a greater stroke width
	 * for better control.
	 */
	drawTracker: function() {
		var series = this,
			options = series.options,
			trackerPath = [].concat(series.graphPath),
			trackerPathLength = trackerPath.length,
			chart = series.chart,
			snap = chart.options.tooltip.snap,
			tracker = series.tracker,
			cursor = options.cursor,
			css = cursor &amp;&amp; { cursor: cursor },
			singlePoints = series.singlePoints,
			singlePoint,
			i;

		// Extend end points. A better way would be to use round linecaps,
		// but those are not clickable in VML.
		if (trackerPathLength) {
			i = trackerPathLength + 1;
			while (i--) {
				if (trackerPath[i] === M) { // extend left side
					trackerPath.splice(i + 1, 0, trackerPath[i + 1] - snap, trackerPath[i + 2], L);
				}
				if ((i &amp;&amp; trackerPath[i] === M) || i === trackerPathLength) { // extend right side
					trackerPath.splice(i, 0, L, trackerPath[i - 2] + snap, trackerPath[i - 1]);
				}
			}
		}

		// handle single points
		for (i = 0; i &lt; singlePoints.length; i++) {
			singlePoint = singlePoints[i];
			trackerPath.push(M, singlePoint.plotX - snap, singlePoint.plotY,
				L, singlePoint.plotX + snap, singlePoint.plotY);
		}

		// draw the tracker
		if (tracker) {
			tracker.attr({ d: trackerPath });

		} else { // create
			series.tracker = chart.renderer.path(trackerPath)
				.attr({
					isTracker: true,
					stroke: TRACKER_FILL,
					fill: NONE,
					'stroke-width' : options.lineWidth + 2 * snap,
					visibility: series.visible ? VISIBLE : HIDDEN,
					zIndex: 1
				})
				.on(hasTouch ? 'touchstart' : 'mouseover', function() {
					if (chart.hoverSeries !== series) {
						series.onMouseOver();
					}
				})
				.on('mouseout', function() {
					if (!options.stickyTracking) {
						series.onMouseOut();
					}
				})
				.css(css)
				.add(chart.trackerGroup);
		}

	}

}; // end Series prototype


/**
 * LineSeries object
 */
var LineSeries = extendClass(Series);
seriesTypes.line = LineSeries;

/**
 * AreaSeries object
 */
var AreaSeries = extendClass(Series, {
	type: 'area',
	useThreshold: true
});
seriesTypes.area = AreaSeries;




/**
 * SplineSeries object
 */
var SplineSeries = extendClass( Series, {
	type: 'spline',

	/**
	 * Draw the actual graph
	 */
	getPointSpline: function(segment, point, i) {
		var smoothing = 1.5, // 1 means control points midway between points, 2 means 1/3 from the point, 3 is 1/4 etc
			denom = smoothing + 1,
			plotX = point.plotX,
			plotY = point.plotY,
			lastPoint = segment[i - 1],
			nextPoint = segment[i + 1],
			leftContX,
			leftContY,
			rightContX,
			rightContY,
			ret;

		// find control points
		if (i &amp;&amp; i &lt; segment.length - 1) {
			var lastX = lastPoint.plotX,
				lastY = lastPoint.plotY,
				nextX = nextPoint.plotX,
				nextY = nextPoint.plotY,
				correction;

			leftContX = (smoothing * plotX + lastX) / denom;
			leftContY = (smoothing * plotY + lastY) / denom;
			rightContX = (smoothing * plotX + nextX) / denom;
			rightContY = (smoothing * plotY + nextY) / denom;

			// have the two control points make a straight line through main point
			correction = ((rightContY - leftContY) * (rightContX - plotX)) /
				(rightContX - leftContX) + plotY - rightContY;

			leftContY += correction;
			rightContY += correction;

			// to prevent false extremes, check that control points are between
			// neighbouring points' y values
			if (leftContY &gt; lastY &amp;&amp; leftContY &gt; plotY) {
				leftContY = mathMax(lastY, plotY);
				rightContY = 2 * plotY - leftContY; // mirror of left control point
			} else if (leftContY &lt; lastY &amp;&amp; leftContY &lt; plotY) {
				leftContY = mathMin(lastY, plotY);
				rightContY = 2 * plotY - leftContY;
			}
			if (rightContY &gt; nextY &amp;&amp; rightContY &gt; plotY) {
				rightContY = mathMax(nextY, plotY);
				leftContY = 2 * plotY - rightContY;
			} else if (rightContY &lt; nextY &amp;&amp; rightContY &lt; plotY) {
				rightContY = mathMin(nextY, plotY);
				leftContY = 2 * plotY - rightContY;
			}

			// record for drawing in next point
			point.rightContX = rightContX;
			point.rightContY = rightContY;

		}

		// moveTo or lineTo
		if (!i) {
			ret = [M, plotX, plotY];
		}

		// curve from last point to this
		else {
			ret = [
				'C',
				lastPoint.rightContX || lastPoint.plotX,
				lastPoint.rightContY || lastPoint.plotY,
				leftContX || plotX,
				leftContY || plotY,
				plotX,
				plotY
			];
			lastPoint.rightContX = lastPoint.rightContY = null; // reset for updating series later
		}
		return ret;
	}
});
seriesTypes.spline = SplineSeries;



/**
 * AreaSplineSeries object
 */
var AreaSplineSeries = extendClass(SplineSeries, {
	type: 'areaspline',
	useThreshold: true
});
seriesTypes.areaspline = AreaSplineSeries;

/**
 * ColumnSeries object
 */
var ColumnSeries = extendClass(Series, {
	type: 'column',
	useThreshold: true,
	getDistance: true,
	pointAttrToOptions: { // mapping between SVG attributes and the corresponding options
		stroke: 'borderColor',
		'stroke-width': 'borderWidth',
		fill: 'color',
		r: 'borderRadius'
	},
	init: function() {
		Series.prototype.init.apply(this, arguments);

		var series = this,
			chart = series.chart;

		// flag the chart in order to pad the x axis
		//chart.hasColumn = true;

		// if the series is added dynamically, force redraw of other
		// series affected by a new column
		if (chart.hasRendered) {
			each(chart.series, function(otherSeries) {
				if (otherSeries.type === series.type) {
					otherSeries.isDirty = true;
				}
			});
		}
	},

	/**
	 * Translate each point to the plot area coordinate system and find shape positions
	 */
	translate: function() {
		var series = this,
			chart = series.chart,
			options = series.options,
			stacking = options.stacking,
			borderWidth = options.borderWidth,
			columnCount = 0,
			xAxis = series.xAxis,
			reversedXAxis = xAxis.reversed,
			categories = xAxis.categories,
			stackGroups = {},
			stackKey,
			columnIndex;

		Series.prototype.translate.apply(series);

		// Get the total number of column type series.
		// This is called on every series. Consider moving this logic to a
		// chart.orderStacks() function and call it on init, addSeries and removeSeries
		each(chart.series, function(otherSeries) {
			if (otherSeries.type === series.type &amp;&amp; otherSeries.visible &amp;&amp;
					series.options.group === otherSeries.options.group) { // used in Stock charts navigator series
				if (otherSeries.options.stacking) {
					stackKey = otherSeries.stackKey;
					if (stackGroups[stackKey] === UNDEFINED) {
						stackGroups[stackKey] = columnCount++;
					}
					columnIndex = stackGroups[stackKey];
				} else {
					columnIndex = columnCount++;
				}
				otherSeries.columnIndex = columnIndex;
			}
		});

		// calculate the width and position of each column based on
		// the number of column series in the plot, the groupPadding
		// and the pointPadding options
		var points = series.points,
			//closestPoints = series.closestPoints || 1,
			categoryWidth = mathAbs(pick(
				xAxis.leastDistance,
				chart.plotSizeX / ((categories &amp;&amp; categories.length) || 1)
			)),
			groupPadding = categoryWidth * options.groupPadding,
			groupWidth = categoryWidth - 2 * groupPadding,
			pointOffsetWidth = groupWidth / columnCount,
			optionPointWidth = options.pointWidth,
			pointPadding = defined(optionPointWidth) ? (pointOffsetWidth - optionPointWidth) / 2 :
				pointOffsetWidth * options.pointPadding,
			pointWidth = mathCeil(mathMax(pick(optionPointWidth, pointOffsetWidth - 2 * pointPadding), 1)),
			colIndex = (reversedXAxis ? columnCount -
				series.columnIndex : series.columnIndex) || 0,
			pointXOffset = pointPadding + (groupPadding + colIndex *
				pointOffsetWidth -(categoryWidth / 2)) *
				(reversedXAxis ? -1 : 1),
			threshold = options.threshold,
			translatedThreshold = series.yAxis.getThreshold(threshold),
			minPointLength = pick(options.minPointLength, 5);

		// record the new values
		each(points, function(point, i) {
			var plotY = point.plotY,
				yBottom = point.yBottom || translatedThreshold,
				barX = point.plotX + pointXOffset,
				barY = mathCeil(mathMin(plotY, yBottom)),
				barH = mathCeil(mathMax(plotY, yBottom) - barY),
				stack = series.yAxis.stacks[(point.y &lt; 0 ? '-' : '') + series.stackKey],
				trackerY,
				shapeArgs;

			// Record the offset'ed position and width of the bar to be able to align the stacking total correctly
			if (stacking &amp;&amp; series.visible &amp;&amp; stack &amp;&amp; stack[point.x]) {
				stack[point.x].setOffset(pointXOffset, pointWidth);
			}

			// handle options.minPointLength and tracker for small points
			if (mathAbs(barH) &lt; minPointLength) {
				if (minPointLength) {
					barH = minPointLength;
					barY =
						mathAbs(barY - translatedThreshold) &gt; minPointLength ? // stacked
							yBottom - minPointLength : // keep position
							translatedThreshold - (plotY &lt;= translatedThreshold ? minPointLength : 0);
				}
				trackerY = barY - 3;
			}

			extend(point, {
				barX: barX,
				barY: barY,
				barW: pointWidth,
				barH: barH
			});

			// create shape type and shape args that are reused in drawPoints and drawTracker
			point.shapeType = 'rect';
			shapeArgs = extend(chart.renderer.Element.prototype.crisp.apply({}, [
				borderWidth,
				barX,
				barY,
				pointWidth,
				barH
			]), {
				r: options.borderRadius
			});
			if (borderWidth % 2) { // correct for shorting in crisp method, visible in stacked columns with 1px border
				shapeArgs.y -= 1;
				shapeArgs.height += 1;
			}
			point.shapeArgs = shapeArgs;

			// make small columns responsive to mouse
			point.trackerArgs = defined(trackerY) &amp;&amp; merge(point.shapeArgs, {
				height: mathMax(6, barH + 3),
				y: trackerY
			});
		});

	},

	getSymbol: function(){
	},

	/**
	 * Columns have no graph
	 */
	drawGraph: function() {},

	/**
	 * Draw the columns. For bars, the series.group is rotated, so the same coordinates
	 * apply for columns and bars. This method is inherited by scatter series.
	 *
	 */
	drawPoints: function() {
		var series = this,
			options = series.options,
			renderer = series.chart.renderer,
			graphic,
			shapeArgs;


		// draw the columns
		each(series.points, function(point) {
			var plotY = point.plotY;
			if (plotY !== UNDEFINED &amp;&amp; !isNaN(plotY) &amp;&amp; point.y !== null) {
				graphic = point.graphic;
				shapeArgs = point.shapeArgs;
				if (graphic) { // update
					stop(graphic);
					graphic.animate(shapeArgs);

				} else {
					point.graphic = graphic = renderer[point.shapeType](shapeArgs)
						.attr(point.pointAttr[point.selected ? SELECT_STATE : NORMAL_STATE])
						.add(series.group)
						.shadow(options.shadow);
				}

			}
		});
	},
	/**
	 * Draw the individual tracker elements.
	 * This method is inherited by scatter and pie charts too.
	 */
	drawTracker: function() {
		var series = this,
			chart = series.chart,
			renderer = chart.renderer,
			shapeArgs,
			tracker,
			trackerLabel = +new Date(),
			cursor = series.options.cursor,
			css = cursor &amp;&amp; { cursor: cursor },
			rel;

		each(series.points, function(point) {
			tracker = point.tracker;
			shapeArgs = point.trackerArgs || point.shapeArgs;
			delete shapeArgs.strokeWidth;
			if (point.y !== null) {
				if (tracker) {// update
					tracker.attr(shapeArgs);

				} else {
					point.tracker =
						renderer[point.shapeType](shapeArgs)
						.attr({
							isTracker: trackerLabel,
							fill: TRACKER_FILL,
							visibility: series.visible ? VISIBLE : HIDDEN,
							zIndex: 1
						})
						.on(hasTouch ? 'touchstart' : 'mouseover', function(event) {
							rel = event.relatedTarget || event.fromElement;
							if (chart.hoverSeries !== series &amp;&amp; attr(rel, 'isTracker') !== trackerLabel) {
								series.onMouseOver();
							}
							point.onMouseOver();

						})
						.on('mouseout', function(event) {
							if (!series.options.stickyTracking) {
								rel = event.relatedTarget || event.toElement;
								if (attr(rel, 'isTracker') !== trackerLabel) {
									series.onMouseOut();
								}
							}
						})
						.css(css)
						.add(point.group || chart.trackerGroup); // pies have point group - see issue #118
				}
			}
		});
	},


	/**
	 * Animate the column heights one by one from zero
	 * @param {Boolean} init Whether to initialize the animation or run it
	 */
	animate: function(init) {
		var series = this,
			points = series.points;

		if (!init) { // run the animation
			/*
			 * Note: Ideally the animation should be initialized by calling
			 * series.group.hide(), and then calling series.group.show()
			 * after the animation was started. But this rendered the shadows
			 * invisible in IE8 standards mode. If the columns flicker on large
			 * datasets, this is the cause.
			 */

			each(points, function(point) {
				var graphic = point.graphic,
					shapeArgs = point.shapeArgs;

				if (graphic) {
					// start values
					graphic.attr({
						height: 0,
						y: series.yAxis.translate(0, 0, 1)
					});

					// animate
					graphic.animate({
						height: shapeArgs.height,
						y: shapeArgs.y
					}, series.options.animation);
				}
			});


			// delete this function to allow it only once
			series.animate = null;
		}

	},
	/**
	 * Remove this series from the chart
	 */
	remove: function() {
		var series = this,
			chart = series.chart;

		// column and bar series affects other series of the same type
		// as they are either stacked or grouped
		if (chart.hasRendered) {
			each(chart.series, function(otherSeries) {
				if (otherSeries.type === series.type) {
					otherSeries.isDirty = true;
				}
			});
		}

		Series.prototype.remove.apply(series, arguments);
	}
});
seriesTypes.column = ColumnSeries;

var BarSeries = extendClass(ColumnSeries, {
	type: 'bar',
	init: function(chart) {
		chart.inverted = this.inverted = true;
		ColumnSeries.prototype.init.apply(this, arguments);
	}
});
seriesTypes.bar = BarSeries;

/**
 * The scatter series class
 */
var ScatterSeries = extendClass(Series, {
	type: 'scatter',

	/**
	 * Extend the base Series' translate method by adding shape type and
	 * arguments for the point trackers
	 */
	translate: function() {
		var series = this;

		Series.prototype.translate.apply(series);

		each(series.points, function(point) {
			point.shapeType = 'circle';
			point.shapeArgs = {
				x: point.plotX,
				y: point.plotY,
				r: series.chart.options.tooltip.snap
			};
		});
	},


	/**
	 * Create individual tracker elements for each point
	 */
	//drawTracker: ColumnSeries.prototype.drawTracker,
	drawTracker: function() {
		var series = this,
			cursor = series.options.cursor,
			css = cursor &amp;&amp; { cursor: cursor },
			graphic;

		each(series.points, function(point) {
			graphic = point.graphic;
			if (graphic) { // doesn't exist for null points
				graphic
					.attr({ isTracker: true })
					.on('mouseover', function(event) {
						series.onMouseOver();
						point.onMouseOver();
					})
					.on('mouseout', function(event) {
						if (!series.options.stickyTracking) {
							series.onMouseOut();
						}
					})
					.css(css);
			}
		});

	}//,

	/**
	 * Cleaning the data is not necessary in a scatter plot
	 */
	//cleanData: function() {}
});
seriesTypes.scatter = ScatterSeries;

/**
 * Extended point object for pies
 */
var PiePoint = extendClass(Point, {
	/**
	 * Initiate the pie slice
	 */
	init: function () {

		Point.prototype.init.apply(this, arguments);

		var point = this,
			toggleSlice;

		//visible: options.visible !== false,
		extend(point, {
			visible: point.visible !== false,
			name: pick(point.name, 'Slice')
		});

		// add event listener for select
		toggleSlice = function() {
			point.slice();
		};
		addEvent(point, 'select', toggleSlice);
		addEvent(point, 'unselect', toggleSlice);

		return point;
	},

	/**
	 * Toggle the visibility of the pie slice
	 * @param {Boolean} vis Whether to show the slice or not. If undefined, the
	 *    visibility is toggled
	 */
	setVisible: function(vis) {
		var point = this,
			chart = point.series.chart,
			tracker = point.tracker,
			dataLabel = point.dataLabel,
			connector = point.connector,
			method;

		// if called without an argument, toggle visibility
		point.visible = vis = vis === UNDEFINED ? !point.visible : vis;

		method = vis ? 'show' : 'hide';

		point.group[method]();
		if (tracker) {
			tracker[method]();
		}
		if (dataLabel) {
			dataLabel[method]();
		}
		if (connector) {
			connector[method]();
		}
		if (point.legendItem) {
			chart.legend.colorizeItem(point, vis);
		}
	},

	/**
	 * Set or toggle whether the slice is cut out from the pie
	 * @param {Boolean} sliced When undefined, the slice state is toggled
	 * @param {Boolean} redraw Whether to redraw the chart. True by default.
	 */
	slice: function(sliced, redraw, animation) {
		var point = this,
			series = point.series,
			chart = series.chart,
			slicedTranslation = point.slicedTranslation,
			translation;

		setAnimation(animation, chart);

		// redraw is true by default
		redraw = pick(redraw, true);

		// if called without an argument, toggle
		sliced = point.sliced = defined(sliced) ? sliced : !point.sliced;

		translation = {
			translateX: (sliced ? slicedTranslation[0] : chart.plotLeft),
			translateY: (sliced ? slicedTranslation[1] : chart.plotTop)
		};
		point.group.animate(translation);
		if (point.shadowGroup) {
			point.shadowGroup.animate(translation);
		}

	}
});

/**
 * The Pie series class
 */
var PieSeries = extendClass(Series, {
	type: 'pie',
	isCartesian: false,
	pointClass: PiePoint,
	pointAttrToOptions: { // mapping between SVG attributes and the corresponding options
		stroke: 'borderColor',
		'stroke-width': 'borderWidth',
		fill: 'color'
	},

	/**
	 * Pies have one color each point
	 */
	getColor: function() {
		// record first color for use in setData
		this.initialColor = this.chart.counters.color;
	},

	/**
	 * Animate the column heights one by one from zero
	 * @param {Boolean} init Whether to initialize the animation or run it
	 */
	animate: function(init) {
		var series = this,
			points = series.points;

		each(points, function(point) {
			var graphic = point.graphic,
				args = point.shapeArgs,
				up = -mathPI / 2;

			if (graphic) {
				// start values
				graphic.attr({
					r: 0,
					start: up,
					end: up
				});

				// animate
				graphic.animate({
					r: args.r,
					start: args.start,
					end: args.end
				}, series.options.animation);
			}
		});

		// delete this function to allow it only once
		series.animate = null;

	},

	/**
	 * Extend the basic setData method by running processData and generatePoints immediately,
	 * in order to access the points from the legend.
	 */
	setData: function() {
		Series.prototype.setData.apply(this, arguments);
		this.processData();
		this.generatePoints();
	},
	/**
	 * Do translation for pie slices
	 */
	translate: function() {
		var total = 0,
			series = this,
			cumulative = -0.25, // start at top
			precision = 1000, // issue #172
			options = series.options,
			slicedOffset = options.slicedOffset,
			connectorOffset = slicedOffset + options.borderWidth,
			positions = options.center.concat([options.size, options.innerSize || 0]),
			chart = series.chart,
			plotWidth = chart.plotWidth,
			plotHeight = chart.plotHeight,
			start,
			end,
			angle,
			points = series.points,
			circ = 2 * mathPI,
			fraction,
			smallestSize = mathMin(plotWidth, plotHeight),
			isPercent,
			radiusX, // the x component of the radius vector for a given point
			radiusY,
			labelDistance = options.dataLabels.distance;

		// get positions - either an integer or a percentage string must be given
		positions = map(positions, function(length, i) {

			isPercent = /%$/.test(length);
			return isPercent ?
				// i == 0: centerX, relative to width
				// i == 1: centerY, relative to height
				// i == 2: size, relative to smallestSize
				// i == 4: innerSize, relative to smallestSize
				[plotWidth, plotHeight, smallestSize, smallestSize][i] *
					pInt(length) / 100:
				length;
		});

		// utility for getting the x value from a given y, used for anticollision logic in data labels
		series.getX = function(y, left) {

			angle = math.asin((y - positions[1]) / (positions[2] / 2 + labelDistance));

			return positions[0] +
				(left ? -1 : 1) *
				(mathCos(angle) * (positions[2] / 2 + labelDistance));
		};

		// set center for later use
		series.center = positions;

		// get the total sum
		each(points, function(point) {
			total += point.y;
		});

		each(points, function(point) {
			// set start and end angle
			fraction = total ? point.y / total : 0;
			start = mathRound(cumulative * circ * precision) / precision;
			cumulative += fraction;
			end = mathRound(cumulative * circ * precision) / precision;

			// set the shape
			point.shapeType = 'arc';
			point.shapeArgs = {
				x: positions[0],
				y: positions[1],
				r: positions[2] / 2,
				innerR: positions[3] / 2,
				start: start,
				end: end
			};

			// center for the sliced out slice
			angle = (end + start) / 2;
			point.slicedTranslation = map([
				mathCos(angle) * slicedOffset + chart.plotLeft,
				mathSin(angle) * slicedOffset + chart.plotTop
			], mathRound);

			// set the anchor point for tooltips
			radiusX = mathCos(angle) * positions[2] / 2;
			radiusY = mathSin(angle) * positions[2] / 2;
			point.tooltipPos = [
				positions[0] + radiusX * 0.7,
				positions[1] + radiusY * 0.7
			];

			// set the anchor point for data labels
			point.labelPos = [
				positions[0] + radiusX + mathCos(angle) * labelDistance, // first break of connector
				positions[1] + radiusY + mathSin(angle) * labelDistance, // a/a
				positions[0] + radiusX + mathCos(angle) * connectorOffset, // second break, right outside pie
				positions[1] + radiusY + mathSin(angle) * connectorOffset, // a/a
				positions[0] + radiusX, // landing point for connector
				positions[1] + radiusY, // a/a
				labelDistance &lt; 0 ? // alignment
					'center' :
					angle &lt; circ / 4 ? 'left' : 'right', // alignment
				angle // center angle
			];


			// API properties
			point.percentage = fraction * 100;
			point.total = total;

		});

		this.setTooltipPoints();
	},

	/**
	 * Render the slices
	 */
	render: function() {
		var series = this;

		// cache attributes for shapes
		series.getAttribs();

		this.drawPoints();

		// draw the mouse tracking area
		if (series.options.enableMouseTracking !== false) {
			series.drawTracker();
		}

		this.drawDataLabels();

		if (series.options.animation &amp;&amp; series.animate) {
			series.animate();
		}

		series.isDirty = series.isDirtyData = false; // means data is in accordance with what you see
	},

	/**
	 * Draw the data points
	 */
	drawPoints: function() {
		var series = this,
			chart = series.chart,
			renderer = chart.renderer,
			groupTranslation,
			//center,
			graphic,
			group,
			shadow = series.options.shadow,
			shadowGroup,
			shapeArgs;

		// draw the slices
		each(series.points, function(point) {
			graphic = point.graphic;
			shapeArgs = point.shapeArgs;
			group = point.group;
			shadowGroup = point.shadowGroup;

			// put the shadow behind all points
			if (shadow &amp;&amp; !shadowGroup) {
				shadowGroup = point.shadowGroup = renderer.g('shadow')
					.attr({ zIndex: 4 })
					.add();
			}

			// create the group the first time
			if (!group) {
				group = point.group = renderer.g('point')
					.attr({ zIndex: 5 })
					.add();
			}

			// if the point is sliced, use special translation, else use plot area traslation
			groupTranslation = point.sliced ? point.slicedTranslation : [chart.plotLeft, chart.plotTop];
			group.translate(groupTranslation[0], groupTranslation[1]);
			if (shadowGroup) {
				shadowGroup.translate(groupTranslation[0], groupTranslation[1]);
			}

			// draw the slice
			if (graphic) {
				graphic.animate(shapeArgs);
			} else {
				point.graphic =
					renderer.arc(shapeArgs)
					.attr(extend(
						point.pointAttr[NORMAL_STATE],
						{ 'stroke-linejoin': 'round' }
					))
					.add(point.group)
					.shadow(shadow, shadowGroup);
			}

			// detect point specific visibility
			if (point.visible === false) {
				point.setVisible(false);
			}

		});

	},

	/**
	 * Override the base drawDataLabels method by pie specific functionality
	 */
	drawDataLabels: function() {
		var series = this,
			points = series.points,
			point,
			chart = series.chart,
			options = series.options.dataLabels,
			connectorPadding = pick(options.connectorPadding, 10),
			connectorWidth = pick(options.connectorWidth, 1),
			connector,
			connectorPath,
			outside = options.distance &gt; 0,
			dataLabel,
			labelPos,
			labelHeight,
			lastY,
			centerY = series.center[1],
			quarters = [// divide the points into quarters for anti collision
				[], // top right
				[], // bottom right
				[], // bottom left
				[] // top left
			],
			x,
			y,
			visibility,
			overlapping,
			rankArr,
			secondPass,
			sign,
			lowerHalf,
			sort,
			i = 4,
			j;

		// run parent method
		Series.prototype.drawDataLabels.apply(series);

		// arrange points for detection collision
		each(points, function(point) {
			var angle = point.labelPos[7],
				quarter;
			if (angle &lt; 0) {
				quarter = 0;
			} else if (angle &lt; mathPI / 2) {
				quarter = 1;
			} else if (angle &lt; mathPI) {
				quarter = 2;
			} else {
				quarter = 3;
			}
			quarters[quarter].push(point);
		});
		quarters[1].reverse();
		quarters[3].reverse();

		// define the sorting algorithm
		sort = function(a,b) {
			return a.y &gt; b.y;
		};
		/* Loop over the points in each quartile, starting from the top and bottom
		 * of the pie to detect overlapping labels.
		 */
		while (i--) {
			overlapping = 0;

			// create an array for sorting and ranking the points within each quarter
			rankArr = [].concat(quarters[i]);
			rankArr.sort(sort);
			j = rankArr.length;
			while (j--) {
				rankArr[j].rank = j;
			}

			/* In the first pass, count the number of overlapping labels. In the second
			 * pass, remove the labels with lowest rank/values.
			 */
			for (secondPass = 0; secondPass &lt; 2; secondPass++) {
				lowerHalf = i % 3;
				lastY = lowerHalf ? 9999 : -9999;
				sign = lowerHalf ? -1 : 1;

				for (j = 0; j &lt; quarters[i].length; j++) {
					point = quarters[i][j];

					dataLabel = point.dataLabel;
					if (dataLabel) {
						labelPos = point.labelPos;
						visibility = VISIBLE;
						x = labelPos[0];
						y = labelPos[1];


						// assume all labels have equal height
						if (!labelHeight) {
							labelHeight = dataLabel &amp;&amp; dataLabel.getBBox().height;
						}

						// anticollision
						if (outside) {
							if (secondPass &amp;&amp; point.rank &lt; overlapping) {
								visibility = HIDDEN;
							} else if ((!lowerHalf &amp;&amp; y &lt; lastY + labelHeight) ||
									(lowerHalf &amp;&amp; y &gt; lastY - labelHeight)) {
								y = lastY + sign * labelHeight;
								x = series.getX(y, i &gt; 1);
								if ((!lowerHalf &amp;&amp; y + labelHeight &gt; centerY) ||
										(lowerHalf &amp;&amp; y -labelHeight &lt; centerY)) {
									if (secondPass) {
										visibility = HIDDEN;
									} else {
										overlapping++;
									}
								}
							}
						}

						if (point.visible === false) {
							visibility = HIDDEN;
						}

						if (visibility === VISIBLE) {
							lastY = y;
						}

						if (secondPass) {

							// move or place the data label
							dataLabel
								.attr({
									visibility: visibility,
									align: labelPos[6]
								})[dataLabel.moved ? 'animate' : 'attr']({
									x: x + options.x +
										({ left: connectorPadding, right: -connectorPadding }[labelPos[6]] || 0),
									y: y + options.y
								});
							dataLabel.moved = true;

							// draw the connector
							if (outside &amp;&amp; connectorWidth) {
								connector = point.connector;

								connectorPath = [
									M,
									x + (labelPos[6] === 'left' ? 5 : -5), y, // end of the string at the label
									L,
									x, y, // first break, next to the label
									L,
									labelPos[2], labelPos[3], // second break
									L,
									labelPos[4], labelPos[5] // base
								];

								if (connector) {
									connector.animate({ d: connectorPath });
									connector.attr('visibility', visibility);

								} else {
									point.connector = connector = series.chart.renderer.path(connectorPath).attr({
										'stroke-width': connectorWidth,
										stroke: options.connectorColor || '#606060',
										visibility: visibility,
										zIndex: 3
									})
									.translate(chart.plotLeft, chart.plotTop)
									.add();
								}
							}
						}
					}
				}
			}
		}
	},

	/**
	 * Draw point specific tracker objects. Inherit directly from column series.
	 */
	drawTracker: ColumnSeries.prototype.drawTracker,

	/**
	 * Pies don't have point marker symbols
	 */
	getSymbol: function() {}

});
seriesTypes.pie = PieSeries;

/* ****************************************************************************
 * Start data grouping module												 *
 ******************************************************************************/
var DATA_GROUPING = 'dataGrouping',
	seriesProto = Series.prototype,
	baseProcessData = seriesProto.processData,
	baseGeneratePoints = seriesProto.generatePoints,
	baseDestroy = seriesProto.destroy;

/**
 * Extend the basic processData method, that crops the data to the current zoom
 * range, with data grouping logic.
 */
seriesProto.processData = function() {
	var series = this,
		options = series.options,
		dataGroupingOptions = options[DATA_GROUPING];

	baseProcessData.apply(this);

	// disabled?
	if (!dataGroupingOptions || dataGroupingOptions.enabled === false) {
		return;
	}

	var i,
		processedXData = series.processedXData,
		processedYData = series.processedYData,
		data = series.data,
		dataOptions = options.data,
		plotSizeX = series.chart.plotSizeX,
		groupPixelWidth = dataGroupingOptions.groupPixelWidth,
		maxPoints = plotSizeX / groupPixelWidth,
		approximation = dataGroupingOptions.approximation,
		summarize = approximation === 'average' || approximation === 'sum',
		dataLength = processedXData.length,
		ohlcData = series.valueCount === 4,
		groupedData = series.groupedData,
		groupedXData = [],
		groupedYData = [];
	
	// clear previous groups
	each (groupedData || [], function(point, i) {
		if (point) {
			groupedData[i] = point.destroy();
		}
	});

	series.hasGroupedData = false;
	if (dataLength &gt; maxPoints) {
		series.hasGroupedData = true;

		series.points = null; // force recreation of point instances in series.translate

		var xMin = processedXData[0],
			xMax = processedXData[dataLength - 1],
			interval = groupPixelWidth * (xMax - xMin) / plotSizeX,
			groupPositions = getTimeTicks(interval, xMin, xMax, null, dataGroupingOptions.units),
			pointX,
			pointY,
			value = UNDEFINED,
			open = null,
			high = null,
			low = null,
			close = null,
			count = 0;
		
		for (i = 0; i &lt; dataLength; i++) {

			// when a new group is entered, summarize and initiate the previous group
			while (groupPositions[1] !== UNDEFINED &amp;&amp; processedXData[i] &gt;= groupPositions[1]) {

				if (approximation === 'average' &amp;&amp; value !== UNDEFINED &amp;&amp; value !== null) {
					value /= count;
				}

				pointX = groupPositions.shift();
				if (value !== UNDEFINED || ohlcData) {
					groupedXData.push(pointX); // todo: just use groupPositions as xData?

					if (ohlcData) {
						groupedYData.push([open, high, low, close]);
						open = high = low = close = null;
					} else {
						groupedYData.push(value);
					}
				}

				value = UNDEFINED;
				count = 0;
			}

			// increase the counters
			pointY = processedYData[i];
			if (summarize &amp;&amp; !ohlcData) { // approximation = 'sum' or 'average', the most frequent
				value = value === UNDEFINED || value === null ? pointY : value + pointY;
			} else if (ohlcData) {
				var index = series.cropStart + i,
					point = (data &amp;&amp; data[index]) || series.pointClass.prototype.applyOptions.apply({}, [dataOptions[index]]);
				if (open === null) { // first point
					open = point.open;
				}
				high = high === null? point.high : mathMax(high, point.high);
				low = low === null ? point.low : mathMin(low, point.low);
				close = point.close; // last point
			} else if (approximation === 'open' &amp;&amp; value === UNDEFINED) {
				value = pointY;
			} else if (approximation === 'high') {
				value = value === UNDEFINED ? pointY : mathMax(value, pointY);
			} else if (approximation === 'low') {
				value = value === UNDEFINED ? pointY : mathMin(value, pointY);
			} else if (approximation === 'close') { // last point
				value = pointY;
			}

			count++;
		}

		// prevent the smoothed data to spill out left and right, and make
		// sure data is not shifted to the left
		if (dataGroupingOptions.smoothed) {
			i = groupedXData.length - 1;
			groupedXData[i] = xMax;
			while (i-- &amp;&amp; i) {
				groupedXData[i] += interval / 2;
			}
			groupedXData[0] = xMin;
		}

		series.tooltipHeaderFormat = dataGroupingOptions.dateTimeLabelFormats[groupPositions.unit[0]];
		series.unit = groupPositions.unit;

	} else {
		groupedXData = processedXData;
		groupedYData = processedYData;
		series.tooltipHeaderFormat = null;
		series.unit = null;
	}

	series.processedXData = groupedXData;
	series.processedYData = groupedYData;

};

seriesProto.generatePoints = function() {
	var series = this;

	baseGeneratePoints.apply(series);

	// record grouped data in order to let it be destroyed the next time 
processData runs
	series.groupedData = series.hasGroupedData ? series.points : null;
};

seriesProto.destroy = function() {
	var series = this,
		groupedData = series.groupedData || [],
		i = groupedData.length;

	while(i--) {
		if (groupedData[i]) {
			groupedData[i].destroy();
		}
	}
	baseDestroy.apply(series);
};


// Extend the plot options
var dateTimeLabelFormats = {
	second: '%A, %b %e, %H:%M:%S',
	minute: '%A, %b %e, %H:%M',
	hour: '%A, %b %e, %H:%M',
	day: '%A, %b %e, %Y',
	week: 'Week from %A, %b %e, %Y',
	month: '%B %Y',
	year: '%Y'
};

// line types
defaultPlotOptions.line[DATA_GROUPING] =
	defaultPlotOptions.spline[DATA_GROUPING] =
	defaultPlotOptions.area[DATA_GROUPING] =
	defaultPlotOptions.areaspline[DATA_GROUPING] = {
		approximation: 'average', // average, open, high, low, close, sum
		groupPixelWidth: 2,
		dateTimeLabelFormats: dateTimeLabelFormats, // todo: move to tooltip 
options?
		// smoothed = false, // enable this for navigator series only
		units: [[
			'millisecond',					// unit name
			[1, 2, 5, 10, 20, 25, 50, 100, 200, 500]
		], [
			'second',						// unit name
			[1, 2, 5, 10, 15, 30]			// allowed multiples
		], [
			'minute',						// unit name
			[1, 2, 5, 10, 15, 30]			// allowed multiples
		], [
			'hour',							// unit name
			[1, 2, 3, 4, 6, 8, 12]			// allowed multiples
		], [
			'day',							// unit name
			[1]								// allowed multiples
		], [
			'week',							// unit name
			[1]								// allowed multiples
		], [
			'month',
			[1, 3, 6]
		], [
			'year',
			null
		]]
};
// bar-like types (OHLC and candleticks inherit this as the classes are 
not yet built)
defaultPlotOptions.column[DATA_GROUPING] = {
		approximation: 'sum',
		groupPixelWidth: 10,
		dateTimeLabelFormats: dateTimeLabelFormats
};
/* 
****************************************************************************

 * End data grouping module												   *
 
******************************************************************************//*
 
****************************************************************************

 * Start OHLC series code													 *
 
*****************************************************************************/

//
 1 - Set default options
defaultPlotOptions.ohlc = merge(defaultPlotOptions.column, {
	lineWidth: 1,
	dataGrouping: {
		groupPixelWidth: 5 // allows to be packed tighter than candlesticks
	},
	states: {
		hover: {
			lineWidth: 3
		}
	}
});

// 2- Create the OHLCPoint object
var OHLCPoint = extendClass(Point, {
	/**
	 * Apply the options containing the x and OHLC data and possible some 
extra properties.
	 * This is called on point init or from point.update. Extends base 
Point by adding
	 * multiple y-like values.
	 *
	 * @param {Object} options
	 */
	applyOptions: function(options) {
		var point = this,
			series = point.series,
			i = 0;


		// object input for example:
		// { x: Date(2010, 0, 1), open: 7.88, high: 7.99, low: 7.02, close: 
7.65 }
		if (typeof options === 'object' &amp;&amp; typeof options.length !== 
'number') {

			// copy options directly to point
			extend(point, options);

			point.options = options;
		}

		// array
		else if (options.length) {
			// with leading x value
			if (options.length === 5) {
				if (typeof options[0] === 'string') {
					point.name = options[0];
				} else if (typeof options[0] === 'number') {
					point.x = options[0];
				}
				i++;
			}
			point.open = options[i++];
			point.high = options[i++];
			point.low = options[i++];
			point.close = options[i++];
		}

		/*
		 * If no x is set by now, get auto incremented value. All points must 
have an
		 * x value, however the y value can be null to create a gap in the 
series
		 */
		point.y = point.high;
		if (point.x === UNDEFINED &amp;&amp; series) {
			point.x = series.autoIncrement();
		}
		return point;
	},

	/**
	 * A specific OHLC tooltip formatter
	 */
	tooltipFormatter: function() {
		var point = this,
			series = point.series;

		return ['<span style="font-weight: bold;">', (point.name || series.name), '</span><br> ',
			'Open: ', point.open, '<br>',
			'High: ', point.high, '<br>',
			'Low: ', point.low, '<br>',
			'Close: ', point.close, '<br>'].join('');

	}

});

// 3 - Create the OHLCSeries object
var OHLCSeries = extendClass(seriesTypes.column, {
	type: 'ohlc',
	valueCount: 4, // four values per point
	pointClass: OHLCPoint,
	useThreshold: false,

	pointAttrToOptions: { // mapping between SVG attributes and the corresponding options
		stroke: 'color',
		'stroke-width': 'lineWidth'
	},


	/**
	 * Translate data points from raw values x and y to plotX and plotY
	 */
	translate: function() {
		var series = this,
			yAxis = series.yAxis;

		seriesTypes.column.prototype.translate.apply(series);

		// do the translation
		each(series.points, function(point) {
			// the graphics
			if (point.open !== null) {
				point.plotOpen = yAxis.translate(point.open, 0, 1);
			}
			if (point.close !== null) {
				point.plotClose = yAxis.translate(point.close, 0, 1);
			}

		});
	},

	/**
	 * Draw the data points
	 */
	drawPoints: function() {
		var series = this,
			points = series.points,
			chart = series.chart,
			pointAttr,
			plotOpen,
			plotClose,
			crispCorr,
			halfWidth,
			path,
			graphic,
			crispX;


		each(points, function(point) {
			if (point.plotY !== UNDEFINED) {

				graphic = point.graphic;
				pointAttr = point.pointAttr[point.selected ? 'selected' : ''];

				// crisp vector coordinates
				crispCorr = (pointAttr['stroke-width'] % 2) / 2;
				crispX = mathRound(point.plotX) + crispCorr;
				halfWidth = mathRound(point.barW / 2);

				// the vertical stem
				path = [
					'M',
					crispX, mathRound(point.yBottom),
					'L',
					crispX, mathRound(point.plotY)
				];
				
				// open
				if (point.open !== null) {
					plotOpen = mathRound(point.plotOpen) + crispCorr;
					path.push(
						'M',
						crispX, plotOpen,
						'L',
						crispX - halfWidth, plotOpen
					);
				}

				// close
				if (point.close !== null) {
					plotClose = mathRound(point.plotClose) + crispCorr;
					path.push(
						'M',
						crispX, plotClose,
						'L',
						crispX + halfWidth, plotClose
					);
				}

				// create and/or update the graphic
				if (graphic) {
					graphic.animate({ d: path });
				} else {
					point.graphic = chart.renderer.path(path)
						.attr(pointAttr)
						.add(series.group);
				}

			}


		});

	},
	
	/**
	 * Disable animation
	 */
	animate: null


});
seriesTypes.ohlc = OHLCSeries;
/* ****************************************************************************
 * End OHLC series code													   *
 *****************************************************************************/


/* ****************************************************************************
 * Start Candlestick series code											  *
 *****************************************************************************/

// 1 - set default options
defaultPlotOptions.candlestick = merge(defaultPlotOptions.column, {
	lineColor: 'black',
	lineWidth: 1,
	upColor: 'white',
	states: {
		hover: {
			lineWidth: 2
		}
	}
});

// 2 - Create the CandlestickSeries object
var CandlestickSeries = extendClass(OHLCSeries, {
	type: 'candlestick',

	/**
	 * One-to-one mapping from options to SVG attributes
	 */
	pointAttrToOptions: { // mapping between SVG attributes and the corresponding options
		fill: 'color',
		stroke: 'lineColor',
		'stroke-width': 'lineWidth'
	},

	/**
	 * Postprocess mapping between options and SVG attributes
	 */
	getAttribs: function() {
		OHLCSeries.prototype.getAttribs.apply(this, arguments);
		var series = this,
			options = series.options,
			stateOptions = options.states,
			upColor = options.upColor,
			seriesDownPointAttr = merge(series.pointAttr);

		seriesDownPointAttr[''].fill = upColor;
		seriesDownPointAttr.hover.fill = stateOptions.hover.upColor || upColor;
		seriesDownPointAttr.select.fill = stateOptions.select.upColor || upColor;

		each(series.points, function(point) {
			if (point.open &lt; point.close) {
				point.pointAttr = seriesDownPointAttr;
			}
		});
	},

	/**
	 * Draw the data points
	 */
	drawPoints: function() {
		var series = this,  //state = series.state,
			points = series.points,
			chart = series.chart,
			pointAttr,
			plotOpen,
			plotClose,
			topBox,
			bottomBox,
			crispCorr,
			crispX,
			graphic,
			path,
			halfWidth;


		each(points, function(point) {

			graphic = point.graphic;
			if (point.plotY !== UNDEFINED) {

				pointAttr = point.pointAttr[point.selected ? 'selected' : ''];

				// crisp vector coordinates
				crispCorr = (pointAttr['stroke-width'] % 2) / 2;
				crispX = mathRound(point.plotX) + crispCorr;
				plotOpen = mathRound(point.plotOpen) + crispCorr;
				plotClose = mathRound(point.plotClose) + crispCorr;
				topBox = math.min(plotOpen, plotClose);
				bottomBox = math.max(plotOpen, plotClose);
				halfWidth = mathRound(point.barW / 2);

				// create the path
				path = [
					'M',
					crispX - halfWidth, bottomBox,
					'L',
					crispX - halfWidth, topBox,
					'L',
					crispX + halfWidth, topBox,
					'L',
					crispX + halfWidth, bottomBox,
					'L',
					crispX - halfWidth, bottomBox,
					'M',
					crispX, bottomBox,
					'L',
					crispX, mathRound(point.yBottom),
					'M',
					crispX, topBox,
					'L',
					crispX, mathRound(point.plotY),
					'Z'
				];

				if (graphic) {
					graphic.animate({ d: path });
				} else {
					point.graphic = chart.renderer.path(path)
						.attr(pointAttr)
						.add(series.group);
				}

			}
		});

	}


});

seriesTypes.candlestick = CandlestickSeries;

/* ****************************************************************************
 * End Candlestick series code												*
 *****************************************************************************/


/* ****************************************************************************
 * Start Flags series code													*
 *****************************************************************************/

var symbols = Renderer.prototype.symbols;

// 1 - set default options
defaultPlotOptions.flags = merge(defaultPlotOptions.column, {
	fillColor: 'white',
	lineWidth: 1,
	//radius: 2,
	shape: 'flag',
	stackDistance: 7,
	states: {
		hover: {
			lineColor: 'black',
			fillColor: '#FCFFC5'
		}
	},
	style: {
		fontSize: '11px',
		fontWeight: 'bold',
		textAlign: 'center'
	},
	y: -30
});

// 2 - Create the CandlestickSeries object
seriesTypes.flags = extendClass(seriesTypes.column, {
	type: 'flags',
	noSharedTooltip: true,
	useThreshold: false,
	/**
	 * Inherit the initialization from base Series
	 */
	init: Series.prototype.init,

	/**
	 * One-to-one mapping from options to SVG attributes
	 */
	pointAttrToOptions: { // mapping between SVG attributes and the corresponding options
		fill: 'fillColor',
		stroke: 'color',
		'stroke-width': 'lineWidth',
		r: 'radius'
	},

	/**
	 * Extend the translate method by placing the point on the related series
	 */
	translate: function() {

		seriesTypes.column.prototype.translate.apply(this);

		var series = this,
			options = series.options,
			chart = series.chart,
			points = series.points,
			cursor = points.length - 1,
			i,
			point,
			lastPoint,
			optionsOnSeries = options.onSeries,
			onSeries = optionsOnSeries &amp;&amp; chart.get(optionsOnSeries),
			onData,
			onPoint;


		// relate to a master series
		if (onSeries) {
			onData = onSeries.points;
			i = onData.length;

			// sort the data points
			points.sort(function(a, b){
				return (a.x - b.x);
			});

			while (i-- &amp;&amp; points[cursor]) {
				point = points[cursor];
				onPoint = onData[i];
				if (onPoint.x &lt;= point.x) {
					point.plotY = onPoint.plotY;
					cursor--;
					i++; // check again for points in the same x position
					if (cursor &lt; 0) {
						break;
					}
				}
			}
		}

		each(points, function(point, i) {
			// place on y axis
			if (!onSeries) {
				point.plotY = chart.plotHeight;
			}
			// if multiple flags appear at the same x, order them into a stack
			lastPoint = points[i - 1];
			if (lastPoint &amp;&amp; lastPoint.plotX === point.plotX) {
				if (lastPoint.stackIndex === UNDEFINED) {
					lastPoint.stackIndex = 0;
				}
				point.stackIndex = lastPoint.stackIndex + 1;
			}

		});


	},

	/**
	 * Draw the markers
	 */
	drawPoints: function(){
		var series = this,
			pointAttr,
			points = series.points,
			chart = series.chart,
			renderer = chart.renderer,
			plotX,
			plotY,
			options = series.options,
			optionsY = options.y,
			shape = options.shape,
			box,
			bBox,
			i,
			point,
			graphic,
			connector,
			stackIndex,
			crisp = (options.lineWidth % 2 / 2),
			anchorX,
			anchorY;

		i = points.length;
		while (i--) {
			point = points[i];
			plotX = point.plotX + crisp;
			stackIndex = point.stackIndex;
			plotY = point.plotY + optionsY + crisp - (stackIndex !== UNDEFINED &amp;&amp; stackIndex * options.stackDistance);
			// outside to the left, on series but series is clipped
			if (isNaN(plotY)) {
				plotY = 0;
			}
			anchorX = stackIndex ? UNDEFINED : point.plotX + crisp; // skip connectors for higher level stacked points
			anchorY = stackIndex ? UNDEFINED : point.plotY;

			graphic = point.graphic;
			connector = point.connector;

			// only draw the point if y is defined
			if (plotY !== UNDEFINED) {
				// shortcuts
				pointAttr = point.pointAttr[point.selected ? 'select' : ''];
				if (graphic) { // update
					graphic.attr({
						x: plotX,
						y: plotY,
						r: pointAttr.r
					});
				} else {
					graphic = point.graphic = renderer.label(
						point.options.title || options.title || 'A',
						plotX,
						plotY,
						shape,
						anchorX,
						anchorY
					)
					.css(merge(options.style, point.style))
					.attr(pointAttr)
					.attr({
						align: shape === 'flag' ? 'left' : 'center',
						width: options.width,
						height: options.height
					})
					.add(series.group);

				}

				// get the bounding box
				box = graphic.box;
				bBox = box.getBBox();

				// set the shape arguments for the tracker element
				point.shapeArgs = extend(
					bBox, {
						x: plotX - (shape === 'flag' ? 0 : box.attr('width') / 2), // flags align left, else align center
						y: plotY
					}
				);

			}

		}

	},

	/**
	 * Extend the column trackers with listeners to expand and contract stacks
	 */
	drawTracker: function() {
		var series = this;

		seriesTypes.column.prototype.drawTracker.apply(series);

		// put each point in front on mouse over, this allows readability of vertically
		// stacked elements as well as tight points on the x axis
		each(series.points, function(point) {
			addEvent(point.tracker.element, 'mouseover', function() {
				point.graphic.toFront();
			});
		});
	},

	/**
	 * Override the regular tooltip formatter by returning the point text given
	 * in the options
	 */
	tooltipFormatter: function(item) {
		return item.point.text;
	},

	/**
	 * Disable animation
	 */
	animate: function() {}

});

// create the flag icon with anchor
symbols.flag = function(x, y, w, h, options) {
	var anchorX = (options &amp;&amp; options.anchorX) || x,
		anchorY = (options &amp;&amp;  options.anchorY) || y;

	return [
		'M', anchorX, anchorY,
		'L', x, y + h,
		x, y,
		x + w, y,
		x + w, y + h,
		x, y + h,
		'M', anchorX, anchorY,
		'Z'
	];
};

// create the circlepin and squarepin icons with anchor
each(['circle', 'square'], function(shape) {
	symbols[shape +'pin'] = function(x, y, w, h, options) {
		
		var anchorX = options &amp;&amp; options.anchorX,
			anchorY = options &amp;&amp;  options.anchorY,
			path = symbols[shape](x, y, w, h);
		
		if (anchorX &amp;&amp; anchorY) {
			path.push('M', x + w / 2, y + h, 'L', anchorX, anchorY);
		}

		//console.trace(x, y, );
		return path;
	};
});

/* ****************************************************************************
 * End Flags series code													  *
 *****************************************************************************/


// constants
var	MOUSEDOWN = hasTouch ? 'touchstart' : 'mousedown',
	MOUSEMOVE = hasTouch ? 'touchmove' : 'mousemove',
	MOUSEUP = hasTouch ? 'touchend' : 'mouseup';




/* ****************************************************************************
 * Start Scroller code														*
 *****************************************************************************/

var buttonGradient = {
		linearGradient: [0, 0, 0, 14],
		stops: [
			[0, '#FFF'],
			[1, '#CCC']
		]
	},
	units = [].concat(defaultPlotOptions.line.dataGrouping.units); // copy 
	
// add more resolution to units
units[4][1] = [1, 2, 3, 4]; // allow more days
units[5][1] = [1, 2, 3]; // allow more weeks

extend(defaultOptions, {
	navigator: {
		//enabled: true,
		handles: {
			backgroundColor: '#FFF',
			borderColor: '#666'
		},
		height: 40,
		margin: 10,
		maskFill: 'rgba(255, 255, 255, 0.75)',
		outlineColor: '#444',
		outlineWidth: 1,
		series: {
			type: 'areaspline',
			color: '#4572A7',
			fillOpacity: 0.4,
			dataGrouping: {
				smoothed: true,
				units: units
			},
			lineWidth: 1,
			marker: {
				enabled: false
			},
			shadow: false
		},
		//top: undefined, // docs
		xAxis: {
			tickWidth: 0,
			lineWidth: 0,
			gridLineWidth: 1,
			tickPixelInterval: 200,
			labels: {
				align: 'left',
				x: 3,
				y: -4
			}
		},
		yAxis: {
			gridLineWidth: 0,
			startOnTick: false,
			endOnTick: false,
			minPadding: 0.1,
			maxPadding: 0.1,
			labels: {
				enabled: false
			},
			title: {
				text: null
			},
			tickWidth: 0
		}
	},
	scrollbar: {
		//enabled: true
		height: hasTouch ? 20 : 14,
		barBackgroundColor: buttonGradient,
		barBorderRadius: 2,
		barBorderWidth: 1,
		barBorderColor: '#666',
		buttonArrowColor: '#666',
		buttonBackgroundColor: buttonGradient,
		buttonBorderColor: '#666',
		buttonBorderRadius: 2,
		buttonBorderWidth: 1,
		rifleColor: '#666',
		trackBackgroundColor: {
			linearGradient: [0, 0, 0, 10],
			stops: [
				[0, '#EEE'],
				[1, '#FFF']
			]
		},
		trackBorderColor: '#CCC',
		trackBorderWidth: 1
		// trackBorderRadius: 0
	}
});

/**
 * The Scroller class
 * @param {Object} chart
 */
function Scroller(chart) {

	var renderer = chart.renderer,
		chartOptions = chart.options,
		navigatorOptions = chartOptions.navigator,
		navigatorEnabled = navigatorOptions.enabled,
		navigatorLeft,
		navigatorSeries,
		scrollbarOptions = chartOptions.scrollbar,
		scrollbarEnabled = scrollbarOptions.enabled,
		grabbedLeft,
		grabbedRight,
		grabbedCenter,
		otherHandlePos,
		dragOffset,
		hasDragged,
		xAxis,
		yAxis,
		zoomedMin,
		zoomedMax,
		range,

		bodyStyle = document.body.style,
		defaultBodyCursor,

		handlesOptions = navigatorOptions.handles,
		height = navigatorEnabled ? navigatorOptions.height : 0,
		outlineWidth = navigatorOptions.outlineWidth,
		scrollbarHeight = scrollbarEnabled ? scrollbarOptions.height : 0,
		outlineHeight = height + scrollbarHeight,
		barBorderRadius = scrollbarOptions.barBorderRadius,
		top = navigatorOptions.top || chart.chartHeight - height - scrollbarHeight - chartOptions.chart.spacingBottom,
		halfOutline = outlineWidth / 2,
		outlineTop,
		plotLeft,
		plotWidth,
		rendered,
		baseSeriesOption = navigatorOptions.baseSeries,
		baseSeries = chart.series[baseSeriesOption] ||
			(typeof baseSeriesOption === 'string' &amp;&amp; chart.get(baseSeriesOption)) ||
			chart.series[0],

		// element wrappers
		leftShade,
		rightShade,
		outline,
		handles = [],
		scrollbarGroup,
		scrollbarTrack,
		scrollbar,
		scrollbarRifles,
		scrollbarButtons = [];

	chart.resetZoomEnabled = false;

	/**
	 * Draw one of the handles on the side of the zoomed range in the navigator
	 * @param {Number} x The x center for the handle
	 * @param {Number} index 0 for left and 1 for right
	 */
	function drawHandle(x, index) {

		var attr = {
				fill: handlesOptions.backgroundColor,
				stroke: handlesOptions.borderColor,
				'stroke-width': 1
			};

		// create the elements
		if (!rendered) {

			// the group
			handles[index] = renderer.g()
				.css({ cursor: 'e-resize' })
				.attr({ zIndex: 3 })
				.add();

			// the rectangle
			renderer.rect(-4.5, 0, 9, 16, 3, 1)
				.attr(attr)
				.add(handles[index]);

			// the rifles
			renderer.path([
					'M',
					-1.5, 4,
					'L',
					-1.5,	12,
					'M',
					0.5, 4,
					'L',
					0.5, 12
				]).attr(attr)
				.add(handles[index]);
		}

		handles[index].translate(plotLeft + scrollbarHeight + parseInt(x, 10), top + height / 2 - 8);
	}

	/**
	 * Draw the scrollbar buttons with arrows
	 * @param {Number} index 0 is left, 1 is right
	 */
	function drawScrollbarButton(index) {

		if (!rendered) {

			scrollbarButtons[index] = renderer.g().add(scrollbarGroup);

			renderer.rect(
				-0.5,
				-0.5,
				scrollbarHeight + 1, // +1 to compensate for crispifying in rect method
				scrollbarHeight + 1,
				scrollbarOptions.buttonBorderRadius,
				scrollbarOptions.buttonBorderWidth
			).attr({
				stroke: scrollbarOptions.buttonBorderColor,
				'stroke-width': scrollbarOptions.buttonBorderWidth,
				fill: scrollbarOptions.buttonBackgroundColor
			}).add(scrollbarButtons[index]);

			renderer.path([
				'M',
				scrollbarHeight / 2 + (index ? -1 : 1), scrollbarHeight / 2 - 3,
				'L',
				scrollbarHeight / 2 + (index ? -1 : 1), scrollbarHeight / 2 + 3,
				scrollbarHeight / 2 + (index ? 2 : -2), scrollbarHeight / 2
			]).attr({
				fill: scrollbarOptions.buttonArrowColor
			}).add(scrollbarButtons[index]);
		}

		// adjust the right side button to the varying length of the scroll track
		if (index) {
			scrollbarButtons[index].attr({
				translateX: plotWidth - scrollbarHeight
			});
		}
	}

	/**
	 * Render the navigator and scroll bar
	 * @param {Number} min X axis value minimum
	 * @param {Number} max X axis value maximum
	 * @param {Number} pxMin Pixel value minimum
	 * @param {Number} pxMax Pixel value maximum
	 */
	function render(min, max, pxMin, pxMax) {
		
		var strokeWidth,
			scrollbarStrokeWidth = scrollbarOptions.barBorderWidth,
			centerBarX;
			

		outlineTop = top + halfOutline;
		plotLeft = chart.plotLeft;
		plotWidth = chart.plotWidth;
		navigatorLeft = plotLeft + scrollbarHeight;

		pxMin = pick(pxMin,	xAxis.translate(min));
		pxMax = pick(pxMax, xAxis.translate(max));

		// set the scroller x axis extremes to reflect the total
		if (rendered &amp;&amp; xAxis.getExtremes) {
			var newExtremes = chart.xAxis[0].getExtremes(),
				oldExtremes = xAxis.getExtremes();

			if (newExtremes.dataMin !== oldExtremes.min ||
					newExtremes.dataMax !== oldExtremes.max) {
				xAxis.setExtremes(newExtremes.dataMin, newExtremes.dataMax);
			}
		}

		// handles are allowed to cross
		zoomedMin = parseInt(mathMin(pxMin, pxMax), 10);
		zoomedMax = parseInt(mathMax(pxMin, pxMax), 10);
		range = zoomedMax - zoomedMin;

		// on first render, create all elements
		if (!rendered) {

			if (navigatorEnabled) {

				leftShade = renderer.rect()
					.attr({
						fill: navigatorOptions.maskFill,
						zIndex: 3
					}).add();
				rightShade = renderer.rect()
					.attr({
						fill: navigatorOptions.maskFill,
						zIndex: 3
					}).add();
				outline = renderer.path()
					.attr({
						'stroke-width': outlineWidth,
						stroke: navigatorOptions.outlineColor,
						zIndex: 3
					})
					.add();
			}

			if (scrollbarEnabled) {
				
				// draw the scrollbar group
				scrollbarGroup = renderer.g().add();

				// the scrollbar track
				strokeWidth = scrollbarOptions.trackBorderWidth;
				scrollbarTrack = renderer.rect().attr({
					y: - strokeWidth % 2 / 2,
					fill: scrollbarOptions.trackBackgroundColor,
					stroke: scrollbarOptions.trackBorderColor,
					'stroke-width': strokeWidth,
					r: scrollbarOptions.trackBorderRadius || 0,
					height: scrollbarHeight					
				}).add(scrollbarGroup);

				// the scrollbar itself
				scrollbar = renderer.rect()
					.attr({
						y: - scrollbarStrokeWidth % 2 / 2,
						height: scrollbarHeight,
						fill: scrollbarOptions.barBackgroundColor,
						stroke: scrollbarOptions.barBorderColor,
						'stroke-width': scrollbarStrokeWidth,
						rx: barBorderRadius,
						ry: barBorderRadius
					})
					.add(scrollbarGroup);

				scrollbarRifles = renderer.path()
					.attr({
						stroke: scrollbarOptions.rifleColor,
						'stroke-width': 1
					})
					.add(scrollbarGroup);
			}
		}

		// place elements
		if (navigatorEnabled) {
			leftShade.attr({
				x: navigatorLeft,
				y: top,
				width: zoomedMin,
				height: height
			});
			rightShade.attr({
				x: navigatorLeft + zoomedMax,
				y: top,
				width: plotWidth - zoomedMax - 2 * scrollbarHeight,
				height: height
			});
			outline.attr({ d: [
				'M',
				plotLeft, outlineTop, // left
				'L',
				navigatorLeft + zoomedMin - halfOutline,	outlineTop, // upper left of zoomed range
				navigatorLeft + zoomedMin - halfOutline,	outlineTop + outlineHeight, // lower left of z.r.
				navigatorLeft + zoomedMax + halfOutline,	outlineTop + outlineHeight, // lower right of z.r.
				navigatorLeft + zoomedMax + halfOutline,	outlineTop, // upper right of z.r.
				plotLeft + plotWidth, outlineTop // right
			]});
			// draw handles
			drawHandle(zoomedMin - halfOutline, 0);
			drawHandle(zoomedMax + halfOutline, 1);
		}

		// draw the scrollbar
		if (scrollbarEnabled) {

			// draw the buttons
			drawScrollbarButton(0);
			drawScrollbarButton(1);
			
			scrollbarGroup.translate(plotLeft, mathRound(outlineTop + height));

			scrollbarTrack.attr({
				width: plotWidth
			});

			scrollbar.attr({
				x: mathRound(scrollbarHeight + zoomedMin) + (scrollbarStrokeWidth % 2 / 2),
				width: range
			});

			centerBarX = scrollbarHeight + zoomedMin + range / 2 - 0.5;

			scrollbarRifles.attr({ d: [
					M,
					centerBarX - 3, scrollbarHeight / 4,
					L,
					centerBarX - 3, 2 * scrollbarHeight / 3,
					M,
					centerBarX, scrollbarHeight / 4,
					L,
					centerBarX, 2 * scrollbarHeight / 3,
					M,
					centerBarX + 3, scrollbarHeight / 4,
					L,
					centerBarX + 3, 2 * scrollbarHeight / 3
				],
				visibility: range &gt; 12 ? VISIBLE : HIDDEN
			});
		}

		rendered = true;
	}

	/**
	 * Set up the mouse and touch events for the navigator and scrollbar
	 */
	function addEvents() {
		addEvent(chart.container, MOUSEDOWN, function(e) {
			e = chart.tracker.normalizeMouseEvent(e);
			var chartX = e.chartX,
				chartY = e.chartY,
				handleSensitivity = hasTouch ? 10 : 7,
				left,
				isOnNavigator;

			if (chartY &gt; top &amp;&amp; chartY &lt; top + height + scrollbarHeight) { // we're vertically inside the navigator
				isOnNavigator = !scrollbarEnabled || chartY &lt; top + height;

				// grab the left handle
				if (isOnNavigator &amp;&amp; math.abs(chartX - zoomedMin - navigatorLeft) &lt; handleSensitivity) {
					grabbedLeft = true;
					otherHandlePos = zoomedMax;
				}

				// grab the right handle
				else if (isOnNavigator &amp;&amp; math.abs(chartX - zoomedMax - navigatorLeft) &lt; handleSensitivity) {
					grabbedRight = true;
					otherHandlePos = zoomedMin;
				}

				// grab the zoomed range
				else if (chartX &gt; navigatorLeft + zoomedMin &amp;&amp; chartX &lt; navigatorLeft + zoomedMax) {
					grabbedCenter = chartX;
					defaultBodyCursor = bodyStyle.cursor;
					bodyStyle.cursor = 'ew-resize';

					dragOffset = chartX - zoomedMin;
				}

				// click on the shaded areas
				else if (chartX &gt; plotLeft &amp;&amp; chartX &lt; plotLeft + plotWidth) {

					if (isOnNavigator) { // center around the clicked point
						left = chartX - navigatorLeft - range / 2;
					} else { // click on scrollbar
						if (chartX &lt; navigatorLeft) { // click left scrollbar button
							left = zoomedMin - mathMin(10, range);
						} else if (chartX &gt; plotLeft + plotWidth - scrollbarHeight)  {
							left = zoomedMin + mathMin(10, range);
						} else {
							// shift the scrollbar by one range
							left = chartX &lt; navigatorLeft + zoomedMin ? // on the left
								zoomedMin - range :
								zoomedMax;
						}
					}
					if (left &lt; 0) {
						left = 0;
					} else if (left + range &gt; plotWidth - 2 * scrollbarHeight) {
						left = plotWidth - range - 2 * scrollbarHeight;
					}
					chart.xAxis[0].setExtremes(
						xAxis.translate(left, true),
						xAxis.translate(left + range, true),
						true,
						false
					);
				}
			}
			if (e.preventDefault) { // tries to drag object when clicking on the shades
				e.preventDefault();
			}
		});

		addEvent(chart.container, MOUSEMOVE, function(e) {
			e = chart.tracker.normalizeMouseEvent(e);
			var chartX = e.chartX;

			// validation for handle dragging
			if (chartX &lt; navigatorLeft) {
				chartX = navigatorLeft;
			} else if (chartX &gt; plotLeft + plotWidth - scrollbarHeight) {
				chartX = plotLeft + plotWidth - scrollbarHeight;
			}

			// drag left handle
			if (grabbedLeft) {
				hasDragged = true;
				render(0, 0, chartX - navigatorLeft, otherHandlePos);

			// drag right handle
			} else if (grabbedRight) {
				hasDragged = true;
				render(0, 0, otherHandlePos, chartX - navigatorLeft);

			// drag scrollbar or open area in navigator
			} else if (grabbedCenter) {
				hasDragged = true;
				if (chartX &lt; dragOffset) { // outside left
					chartX = dragOffset;
				} else if (chartX &gt; plotWidth + dragOffset - range - 2 * scrollbarHeight) { // outside right
					chartX = plotWidth + dragOffset - range - 2 * scrollbarHeight;
				}

				render(0, 0, chartX - dragOffset, chartX - dragOffset + range);
			}
		});

		addEvent(document, MOUSEUP, function() {
			if (hasDragged) {
				chart.xAxis[0].setExtremes(
					xAxis.translate(zoomedMin, true),
					xAxis.translate(zoomedMax, true),
					true,
					false
				);
			}
			grabbedLeft = grabbedRight = grabbedCenter = hasDragged = dragOffset = null;
			bodyStyle.cursor = defaultBodyCursor;
		});
	}

	/**
	 * Initiate the Scroller object
	 */
	function init() {
		var xAxisIndex = chart.xAxis.length,
			yAxisIndex = chart.yAxis.length;

		// make room below the chart
		chart.extraBottomMargin = outlineHeight + navigatorOptions.margin;

		if (navigatorEnabled) {
			var baseOptions = baseSeries.options,
				navigatorSeriesOptions,
				data = baseOptions.data;

			baseOptions.data = null; // remove it to prevent merging one by one

			navigatorSeriesOptions = merge(baseSeries.options, navigatorOptions.series, {
				threshold: null, // docs
				clip: false, // docs
				enableMouseTracking: false,
				group: 'nav', // for columns
				padXAxis: false,
				xAxis: xAxisIndex,
				yAxis: yAxisIndex,
				name: 'Navigator',
				showInLegend: false
			});

			baseOptions.data = navigatorSeriesOptions.data = data;

			// add the series
			navigatorSeries = chart.initSeries(navigatorSeriesOptions);

			// respond to updated data in the base series
			// todo: use similiar hook when base series is not yet initialized
			addEvent(baseSeries, 'updatedData', function() {
				var baseExtremes = baseSeries.xAxis.getExtremes(),
					range = baseExtremes.max - baseExtremes.min,
					stickToMax = baseExtremes.max &gt;=
						navigatorSeries.xData[navigatorSeries.xData.length - 1],
					stickToMin = baseExtremes.min - range &lt;=
						navigatorSeries.xData[0],
					newMax,
					newMin;

				// set the navigator series data to the new data of the base series
				navigatorSeries.options.pointStart = baseSeries.xData[0];
				navigatorSeries.setData(baseSeries.options.data);

				// if the selection is already at the max, move it to the right as new data
				// comes in
				if (stickToMax) {
					newMax = baseExtremes.dataMax;
					baseSeries.xAxis.setExtremes(newMax - range, newMax);
				} else if (stickToMin) {
					newMin = baseExtremes.dataMin;
					baseSeries.xAxis.setExtremes(newMin, newMin + range);
				// if not, just move the scroller window to reflect the new series data
				} else {
					render(
						mathMax(baseExtremes.min, baseExtremes.dataMin),
						mathMin(baseExtremes.max, baseExtremes.dataMax)
					);

				}
			});

			// an x axis is required for scrollbar also
			xAxis = new chart.Axis(merge(navigatorOptions.xAxis, {
				isX: true,
				type: 'datetime',
				index: xAxisIndex,
				height: height, // docs + width
				top: top, // docs + left
				offset: 0,
				offsetLeft: scrollbarHeight, // docs
				offsetRight: -scrollbarHeight, // docs
				startOnTick: false,
				endOnTick: false,
				minPadding: 0,
				maxPadding: 0,
				zoomEnabled: false
			}));

			yAxis = new chart.Axis(merge(navigatorOptions.yAxis, {
				alignTicks: false, // docs
				height: height,
				top: top,
				offset: 0,
				index: yAxisIndex,
				zoomEnabled: false
			}));

		// in case of scrollbar only, fake an x axis to get translation
		} else {
			xAxis = {
				translate: function(value, reverse) {
					var ext = baseSeries.xAxis.getExtremes(),
						scrollTrackWidth = chart.plotWidth - 2 * scrollbarHeight,
						dataMin = ext.dataMin,
						valueRange = ext.dataMax - dataMin;

					return reverse ?
						// from pixel to value
						(value * valueRange / scrollTrackWidth) + dataMin :
						// from value to pixel
						scrollTrackWidth * (value - dataMin) / valueRange;
				}
			};
		}

		addEvents();
	}


	// Run scroller
	init();

	// Expose
	return {
		render: render
	};

}

/* ****************************************************************************
 * End Scroller code														  *
 *****************************************************************************/

/* ****************************************************************************
 * Start Range Selector code												  *
 *****************************************************************************/
extend(defaultOptions, {
	rangeSelector: {
		// enabled: true,
		// buttons: {Object}
		// buttonTheme: {
		//	states: {
		//		hover: {},
		//		select: {}
		//	}
		// },
		// inputEnabled: true,
		// inputStyle: {}
		// labelStyle: {}
		// selected: undefined
		// todo:
		// - button styles for normal, hover and select state
		// - CSS text styles
		// - styles for the inputs and labels
	}
});
defaultOptions.lang = merge(defaultOptions.lang, {
	rangeSelectorZoom: 'Zoom',
	rangeSelectorFrom: 'From:',
	rangeSelectorTo: 'To:'
});

/**
 * The object constructor for the range selector
 * @param {Object} chart
 */
function RangeSelector(chart) {
	var renderer = chart.renderer,
		rendered,
		container = chart.container,
		lang = defaultOptions.lang,
		div,
		leftBox,
		rightBox,
		selected,
		buttons = [],
		buttonOptions,
		options,
		defaultButtons = [{
			type: 'month',
			count: 1,
			text: '1m'
		}, {
			type: 'month',
			count: 3,
			text: '3m'
		}, {
			type: 'month',
			count: 6,
			text: '6m'
		}, {
			type: 'ytd',
			text: 'YTD'
		}, {
			type: 'year',
			count: 1,
			text: '1y'
		}, {
			type: 'all',
			text: 'All'
		}];
		chart.resetZoomEnabled = false;

	/**
	 * The method to run when one of the buttons in the range selectors is clicked
	 * @param {Number} i The index of the button
	 * @param {Object} rangeOptions
	 * @param {Boolean} redraw
	 */
	function clickButton(i, rangeOptions, redraw) {

		var baseAxis = chart.xAxis[0],
			extremes = baseAxis &amp;&amp; baseAxis.getExtremes(),
			now,
			dataMin = extremes &amp;&amp; extremes.dataMin,
			dataMax = extremes &amp;&amp; extremes.dataMax,
			newMin,
			newMax = baseAxis &amp;&amp; mathMin(extremes.max, dataMax),
			date = new Date(newMax),
			type = rangeOptions.type,
			count = rangeOptions.count,
			range,
			rangeMin,
			year,
			// these time intervals have a fixed number of milliseconds, as opposed
			// to month, ytd and year
			fixedTimes = {
				millisecond: 1,
				second: 1000,
				minute: 60 * 1000,
				hour: 3600 * 1000,
				day: 24 * 3600 * 1000,
				week: 7 * 24 * 3600 * 1000
			};

		// chart has no data, base series is removed
		if (dataMin === null || dataMax === null) {
			return;
		}

		if (fixedTimes[type]) {
			range = fixedTimes[type] * count;
			newMin = mathMax(newMax - range, dataMin);
		}
		else if (type === 'month') {
			date.setMonth(date.getMonth() - count);
			newMin = mathMax(date.getTime(), dataMin);
			range = 30 * 24 * 3600 * 1000 * count;
		}
		else if (type === 'ytd') {
			date = new Date(0);			
			now = new Date();
			year = now.getFullYear();
			date.setFullYear(year);
			
			// workaround for IE6 bug, which sets year to next year instead of current
			if (String(year) !== dateFormat('%Y', date)) {
				date.setFullYear(year - 1);
			}
			
			newMin = rangeMin = mathMax(dataMin || 0, date.getTime());
			now = now.getTime();
			newMax = mathMin(dataMax || now, now);
		}
		else if (type === 'year') {
			date.setFullYear(date.getFullYear() - count);
			newMin = mathMax(dataMin, date.getTime());
			range = 365 * 24 * 3600 * 1000 * count;
		}
		else if (type === 'all' &amp;&amp; baseAxis) {
			newMin = dataMin;
			newMax = dataMax;
		}

		// mark the button pressed
		if (buttons[i]) {
			buttons[i].setState(2);
		}

		// update the chart
		if (!baseAxis) { // axis not yet instanciated
			chart.options.xAxis = merge(
				chart.options.xAxis, {
					//zoomedRange: {
						range: range,
						min: rangeMin
					//}
				}
			);
			selected = i;

		} else { // existing axis object; after render time
			setTimeout(function() { // make sure the visual state is set before the heavy process begins
				baseAxis.setExtremes(
					newMin,
					newMax,
					pick(redraw, 1),
					0
				);
				selected = i;
			}, 1);
		}

	}

	/**
	 * Initialize the range selector
	 */
	function init() {
		chart.extraTopMargin = 25;
		options = chart.options.rangeSelector;
		buttonOptions = options.buttons || defaultButtons;
		selected = options.selected;

		addEvent(container, MOUSEDOWN, function() {

			if (leftBox) {
				leftBox.blur();
			}
			if (rightBox) {
				rightBox.blur();
			}
		});

		// zoomed range based on a pre-selected button index
		if (selected !== UNDEFINED &amp;&amp; buttonOptions[selected]) {
			clickButton(selected, buttonOptions[selected], false);
		}

		// normalize the pressed button whenever a new range is selected
		addEvent(chart, 'load', function() {
			addEvent(chart.xAxis[0], 'setExtremes', function() {
				if (buttons[selected]) {
					buttons[selected].setState(0);
				}
			});
		});
	}


	/**
	 * Set the internal and displayed value of a HTML input for the dates
	 * @param {Object} input
	 * @param {Number} time
	 */
	function setInputValue(input, time) {
		var format = input.hasFocus ? '%Y-%m-%d' : '%b %e, %Y';
		if (time) {
			input.HCTime = time;
		}
		input.value = dateFormat(format, input.HCTime);
	}

	/**
	 * Draw either the 'from' or the 'to' HTML input box of the range selector
	 * @param {Object} name
	 */
	function drawInput(name) {
		var isMin = name === 'min',
			input;

		// create the text label
		createElement('span', {
			innerHTML: lang[isMin ? 'rangeSelectorFrom' : 'rangeSelectorTo']
		}, options.labelStyle, div);

		// create the input element
		input = createElement('input', {
			name: name,
			className: 'highcharts-range-selector',
			type: 'text'
		}, extend({
			width: '80px',
			height: '16px',
			border: '1px solid silver',
			marginLeft: '5px',
			marginRight: isMin ? '5px' : '0',
			textAlign: 'center'
		}, options.inputStyle), div);


		input.onfocus = input.onblur = function(e) {
			e = e || window.event;
			input.hasFocus = e.type === 'focus';
			setInputValue(input);
		};

		// handle changes in the input boxes
		input.onchange = function() {
			var inputValue = input.value,
				value = Date.parse(inputValue),
				extremes = chart.xAxis[0].getExtremes();

			// if the value isn't parsed directly to a value by the browser's Date.parse method,
			// like YYYY-MM-DD in IE, try parsing it a different way
			if (isNaN(value)) {
				value = inputValue.split('-');
				value = Date.UTC(pInt(value[0]), pInt(value[1]) - 1, pInt(value[2]));
			}
			
			if (!isNaN(value) &amp;&amp;
				((isMin &amp;&amp; (value &gt; extremes.dataMin &amp;&amp; value &lt; rightBox.HCTime)) ||
				(!isMin &amp;&amp; (value &lt; extremes.dataMax &amp;&amp; value &gt; leftBox.HCTime)))
			) {
				chart.xAxis[0].setExtremes(
					isMin ? value : extremes.min,
					isMin ? extremes.max : value
				);
			}
		};

		return input;
	}

	/**
	 * Render the range selector including the buttons and the inputs. The first time render
	 * is called, the elements are created and positioned. On subsequent calls, they are
	 * moved and updated.
	 * @param {Number} min X axis minimum
	 * @param {Number} max X axis maximum
	 */
	function render(min, max) {
		var chartStyle = chart.options.chart.style,
			buttonTheme = options.buttonTheme,
			inputEnabled = options.inputEnabled !== false,
			states = buttonTheme &amp;&amp; buttonTheme.states;

		// create the elements
		if (!rendered) {
			renderer.text(lang.rangeSelectorZoom, chart.plotLeft, chart.plotTop - 10)
				.css(options.labelStyle)
				.add();
			
			each(buttonOptions, function(rangeOptions, i) {
				buttons[i] = renderer.button(
					rangeOptions.text,
					chart.plotLeft + 50 +  i * 30,
					chart.plotTop - 25,
					function() {
						clickButton(i, rangeOptions);
						this.isActive = true;
					},
					extend(buttonTheme, {
						padding: 1,
						r: 0
					}),
					states &amp;&amp; states.hover,
					states &amp;&amp; states.select
				)
				.attr({
					width: 28,
					height: 16
				})
				.css({
					textAlign: 'center'
				})
				.add();

				if (selected === i) {
					buttons[i].setState(2);
				}

			});

			// first create a wrapper outside the container in order to make
			// the inputs work and make export correct
			if (inputEnabled) {
				div = createElement('div', null, {
					position: 'relative',
					height: 0,
					fontFamily: chartStyle.fontFamily,
					fontSize: chartStyle.fontSize
				}, container.parentNode);

				// create an absolutely positionied div to keep the inputs
				div = createElement('div', null, extend({
					position: 'absolute',
					top: (-chart.chartHeight + chart.plotTop - 25) +'px',
					right: (chart.chartWidth - chart.plotLeft - chart.plotWidth) + 'px'
				}, options.inputBoxStyle), div);

				leftBox = drawInput('min');

				rightBox = drawInput('max');
			}
		}

		if (inputEnabled) {
			setInputValue(leftBox, min);
			setInputValue(rightBox, max);
		}


		rendered = true;
	}



	// Run RangeSelector
	init();

	// Expose
	return {
		render: render
	};
}

/* ****************************************************************************
 * End Range Selector code													*
 *****************************************************************************/

/*addEvent(Chart.prototype, 'init', function(e) {
	var chart = e.target,
		chartOptions = chart.options;

	// initiate the range selector
	if (chartOptions.rangeSelector.enabled) {
		chart.rangeSelector = new RangeSelector(chart);
	}
});
addEvent(Chart.prototype, 'beforeRender', function(e) {
	var chart = e.target,
		chartOptions = chart.options;

	// initiate the scroller
	if (chartOptions.navigator.enabled || chartOptions.scrollbar.enabled) {
		chart.scroller = new Scroller(chart);
	}
});*/

Chart.prototype.callbacks.push(function(chart) {
	var extremes,
		scroller = chart.scroller,
		rangeSelector = chart.rangeSelector;

	function renderScroller() {
		extremes = chart.xAxis[0].getExtremes();
		scroller.render(
			mathMax(extremes.min, extremes.dataMin),
			mathMin(extremes.max, extremes.dataMax)
		);
	}

	function renderRangeSelector() {
		extremes = chart.xAxis[0].getExtremes();
		rangeSelector.render(extremes.min, extremes.max);
	}

	// initiate the scroller
	if (scroller) {

		// redraw the scroller on setExtremes
		addEvent(chart.xAxis[0], 'setExtremes', function(e) {
			scroller.render(e.min, e.max);
		});

		// redraw the scroller chart resize
		addEvent(chart, 'resize', renderScroller);


		// do it now
		renderScroller();

	}
	if (rangeSelector) {



		// redraw the scroller on setExtremes
		addEvent(chart.xAxis[0], 'setExtremes', function(e) {
			rangeSelector.render(e.min, e.max);
		});

		// redraw the scroller chart resize
		addEvent(chart, 'resize', renderRangeSelector);


		// do it now
		renderRangeSelector();

	}
});

/**
 * A wrapper for Chart with all the default values for a Stock chart
 */
Highcharts.StockChart = function(options, callback) {
	var seriesOptions = options.series, // to increase performance, don't merge the data
		lineOptions = {

			marker: {
				enabled: false,
				states: {
					hover: {
						enabled: true,
						radius: 5
					}
				}
			},
			shadow: false,
			states: {
				hover: {
					lineWidth: 2
				}
			}
		};

	// apply Y axis options to both single and multi y axes
	options.yAxis = map (splat(options.yAxis || {}), function(yAxisOptions) {
		return merge({
			labels: {
				align: 'left',
				x: 2,
				y: -2
			},
			showLastLabel: false,
			title: {
				text: null
			}
		}, yAxisOptions);
	});

	options.series = null;

	options = merge({
		chart: {
			panning: true // docs
		},
		navigator: {
			enabled: true
		},
		scrollbar: {
			enabled: true
		},
		rangeSelector: {
			enabled: true
		},
		title: {
			text: null
		},
		tooltip: {
			shared: true,
			crosshairs: true
		},
		legend: {
			enabled: false
		},
		xAxis: {
			title: {
				text: null
			},
			showLastLabel: true
		},

		plotOptions: {
			line: lineOptions,
			spline: lineOptions,
			area: lineOptions,
			areaspline: lineOptions,
			column: {
				shadow: false,
				borderWidth: 0
			}
		}

	},
	options, // user's options

	{ // forced options
		chart: {
			inverted: false
		},
		xAxis: {
			type: 'datetime',
			categories: null
		}
	});

	options.series = seriesOptions;


	return new Chart(options, callback);
};


// global variables
extend(Highcharts, {
	Chart: Chart,
	dateFormat: dateFormat,
	pathAnim: pathAnim,
	getOptions: getOptions,
	numberFormat: numberFormat,
	Point: Point,
	Color: Color,
	Renderer: Renderer,
	seriesTypes: seriesTypes,
	setOptions: setOptions,
	Series: Series,

	// Expose utility funcitons for modules
	addEvent: addEvent,
	removeEvent: removeEvent,
	createElement: createElement,
	discardElement: discardElement,
	css: css,
	each: each,
	extend: extend,
	map: map,
	merge: merge,
	pick: pick,
	splat: splat,
	extendClass: extendClass,
	version: 'Highstock 1.0 Beta'
});
}());
</hcv:');></stroke></shape></stroke></br.*?></a></span></body></html>