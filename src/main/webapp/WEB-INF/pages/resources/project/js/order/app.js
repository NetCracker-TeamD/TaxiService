var App = (function () {
    var
    //TODO: add validators
    //TODO: add more pages
        blocks = {
            "body": null,
            "header": null,
            "content": null
        },
        controls = {},
        showLoadPage = function () {
            blocks.content.html("")
            blocks.content.append(Templates.getLoader("Page is loading, please wait"))
        },
        loadPage = function (pageName) {
            showLoadPage()
            if (pageName == "new-order") {
                var loader = new Loader()
                loader.addCallBack(function () {
                    console.log("hi")
                    showOrderPage(blocks.content)
                })
                DataTools.init(null, loader)

            }
        },
        updateRoutes = function (invoker, place) {
            var id = $(invoker).attr("data-number"),
                location = place.geometry.location
            MapTools.addMarker(id, location)
            MapTools.markersFitWindow()
        }
    updateLocationsLists = function () {
        var locationsList = DataTools.getFavLocations()
        if (locationsList.length > 0) {
            $.each(blocks.content.find('[data-type="address-group"] .input-group'), function (i, group) {
                var igb = $(group).find(".input-group-btn"),
                    isRemovable = false
                if (igb.has('[data-action="remove"]').length > 0) {
                    isRemovable = true
                }
                igb.remove()
                $(group).append(Templates.getDropDownAddressHTML(locationsList, isRemovable))
            })
        }
    },
        addressesClick = function (e) {
            var target = $(e.target),
                tagName = target[0].tagName.toLowerCase()
            //select fav location part
            if (tagName == "a" || tagName == "small") {
                var input = target.closest('.input-group').find('input'),
                    text_container = target;
                if (tagName == "a") {
                    text_container = target.find("small")
                }
                if (text_container.closest('li').attr('data-action') == "none") {
                    return;
                }
                //.trigger('change'); is important whe you change value programly onChange event doesn`t calls
                input.val(text_container.html()).trigger('change')
                return;
            }
            //remove and add address part
            if (tagName == "button" || tagName == "span") {
                var btn = target
                if (tagName == "span") {
                    btn = target.closest("button")
                }
                if (btn.attr('data-action') == "remove") {
                    var container = target.closest('.input-group'),
                        input = container.find('input'),
                        markerId = input.attr("data-number")
                    MapTools.removeMarker(markerId)
                    MapTools.markersFitWindow()
                    container.remove()
                    return;
                } else if (btn.attr('data-action') == "add") {
                    var startNumber = btn.attr('data-start-number'),
                        hasCarsAmount = (btn.attr('data-mod') == "addCarsAmount"),
                        name = btn.attr("data-name"),
                        newAddressField = Templates.getAddress(DataTools.getFavLocations(), name, hasCarsAmount, true, startNumber),
                        input = newAddressField.find("input")[0]
                    $(target.parent()).find('[data-type="address-group"]').append(newAddressField)
                    MapTools.modAutocompleteAddressInput(input, updateRoutes)
                    return;
                }
            }
        },
        createInputsForServiceType = function (holder, serviceDescription, featuresList) {
            MapTools.clearAllMarker()
            MapTools.markersFitWindow()
            var SD = serviceDescription
            //create DOM
            holder.html("")
            //add contacts
            holder.append(Templates.getContacts())
            holder.append(Templates.getTime(SD.timing.indexOf("now") > -1, SD.timing.indexOf("specified") > -1))
            var locationsList = DataTools.getFavLocations()
            //add addresses
            var addresses = $(Templates.getAddressesContainer())
            var mult = SD.multipleSourceLocations
            var addrGroup = $(Templates.getAddressesGroup("Source:", "start_addresses", mult, mult, 0))
            addrGroup.find('[data-type="address-group"]').append(Templates.getAddress(locationsList, "start_addresses", mult, false))
            addresses.append(addrGroup)

            //console.log(SD)
            if (SD.chain) {
                addrGroup = $(Templates.getAddressesGroup("Intermediate:", "intermediate_addresses", true, false, 100))
                addresses.append(addrGroup)
            }

            if (SD.destinationRequired) {
                var mult = SD.multipleDestinationLocations
                addrGroup = $(Templates.getAddressesGroup("Destination:", "destination_addresses", mult, mult, 1000))
                addrGroup.find('[data-type="address-group"]').append(Templates.getAddress(locationsList, "destination_addresses", false, 1000))
                addresses.append(addrGroup)
            }

            holder.append(addresses)
            //add cars number input
            if (SD.specifyCarsNumbers && !SD.multipleDestinationLocations && !SD.multipleSourceLocations) {
                holder.append(Templates.getCarsAmount(SD.minCarsNumbers))
            }

            var features = Templates.getFeaturesContainer()
            features.append()
            for (var i in featuresList) {
                feature = featuresList[i]
                if (feature.isCategory == true) {
                    features.append(Templates.getFeaturesGroup(feature, "features"))
                } else {
                    features.append(Templates.getFeaturesItem(feature, "features"))
                }
            }
            holder.append(features)

            holder.find('input[data-type="address"]').each(function (i, input) {
                MapTools.modAutocompleteAddressInput(input, updateRoutes)
            })

            //bind events
            addresses.bind("click", addressesClick)
        },
        showOrderPage = function (holder) {
            var container = Templates.getOrderPage(),
                serviceTypesList = DataTools.getServiceTypes(),
                serviceTypes = container.find("#serviceType"),
                orderDetails = container.find('[data-type="order-details"]'),
                map = container.find('[data-type="map"]'),
                orderForm = container.find('#orderForm'),
                makeOrderBtn = container.find('[data-action="make-order"]')

            makeOrderBtn.bind("click", function (e) {
                //TODO : add validation
                var method = orderForm.attr("method").toLowerCase(),
                    url = orderForm.attr("action"),
                    data = JSON.stringify(orderForm.serializeObject())
                method = (method != "get" && method != "post") ? "post" : method
                console.log(data)
                $.ajax({
                    type: method,
                    url: url,
                    contentType: "application/json; charset=utf-8",
                    data: data,
                    cache: false,
                    processData: false,
                    success: function (response) {
                        console.log("response is '" + response + "'")
                        var watchIt = function () {
                            console.log("go to track link")
                        }
                        BootstrapDialog.show({
                            type: BootstrapDialog.TYPE_SUCCESS,
                            title: "Order successfully created",
                            closable: false,
                            message: function (dialog) {
                                var msg = $("<div>Your order successfully created<br>You can track it via this link </div>"),
                                    link = $("<a href='#''>some link with tracknumber</a>")
                                link.bind("click", function (e) {
                                    dialog.close()
                                    watchIt()
                                })
                                msg.append(link)
                                return msg
                            },
                            buttons: [
                                {
                                    label: "Watch it",
                                    action: function (dialog) {
                                        dialog.close()
                                        watchIt()
                                    }
                                }
                            ],
                        })
                    },
                    error: function (response) {
                        console.log(response)
                        BootstrapDialog.show({
                            type: BootstrapDialog.TYPE_DANGER,
                            title: "Server error",
                            message: "Server returns error with status '" + response.statusText + "'",
                        })
                    }
                })
            })

            //fill service types
            $.each(serviceTypesList, function (i, item) {
                serviceTypes.append($('<option>', {
                    value: item.id,
                    text: item.name
                }))
            })

            serviceTypes.bind("change", function (e) {
                var newServiceType = $(e.target).find('option[value="' + $(e.target).val() + '"]').text()
                //console.log(newServiceType)
                createInputsForServiceType(orderDetails,
                    DataTools.getServiceDescription(newServiceType),
                    DataTools.getFeatureList(newServiceType))
            })

            //fill page
            holder.html("")
            holder.append(container)

            //[0] coz new google.maps.Map accepts clear html element, not wraped by jquery
            MapTools.init(map[0])

            serviceTypes.trigger("change")

            updateLocationsLists()
        },
        initBasePage = function () {
            //binding elements
            blocks.header = Templates.getHeader()
            blocks.content = Templates.getContentContainer()
            blocks.body = $("body")
            //fill page
            blocks.body.html("")
                .append(blocks.header)
                .append(blocks.content)
        },
        init = function () {
            MapTools.addListener("onGeolocationAllowed", function (pos) {
                DataTools.setUserLocation("Your location", pos.latitude + ', ' + pos.longitude, true)
                updateLocationsLists()
            })
            initBasePage()
            loadPage("new-order")
        }

    return public_interface = {
        "init": init,
    }
})()


$(document).ready(function () {
    App.init()
})
  