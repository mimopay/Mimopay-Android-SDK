Mimopay Android SDK release notes
================================

1.2.4
------
* fix BadTokenException problem
* ATM is now supported, but UI mode only at the moment

1.2.3
------
* fix next button, when some denominations are overlap screen size

1.2.2
------
* add scrollable to all UI Forms

1.2.1
------
* fix bug when SDK running without storage (sdcard). all logos replaced by text

1.2
-----
* Bug fixes
* Now supoort XL, airtime and voucher
* Introduce new error, ErrorHTTP404NotFound, error return 404
* entities-base.properties and entities-full.properties problem solution added
* Encrypted secretKey is now provided

1.1
-----
* Bug fixes
* Allow wifi-only device keep continue with UPoint transaction
* Introduce new errors
	- UnsupportedPaymentMethod
	- UnspecifiedChannelRequest
* Fix unproportional UI with different device screen size
* Custom radio buttons, works under froyo to jellybean
* Add marquee effect on device that has small screen size
* New scenario on UPoint. allow user to SMS with other device
* Add autosendsms to UPoint quiet mode
* Add save last result
* Add enableLogcat for debugging purpose

1.0
-----
* Initial release!

