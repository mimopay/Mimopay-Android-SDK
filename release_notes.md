Mimopay Android SDK release notes
================================

2.0
------
* improve a lot of things: it looks (UI), process speed, and size

1.3.4
------
* logcat other important vars for troubleshooting purpose
* denom values, voucher codes, and phone number validation check

1.3.3
------
* atm bersama is working now

1.3.2
------
* last result saved to internal

1.3.1
------
* re-increase connection time-out, twice then before

1.3.0
------
* fix minor bug
* In the sample, rename lib to libs.
* increase connection time-out
* remove retry

1.2.9
------
* build-in UI improved
* standardize onReturn's info, all in capital letter

1.2.8
------
* Remove unused debug info
* fix bug on XL, for android honeycomb above

1.2.7
------
* All logos will be shown, even without SDCARD

1.2.6
------
* ATMs (BCA & Bersama) Quiet Mode is now supported

1.2.5
------
* UI improved
* All alert words translated to bahasa
* Add description on top up channels, including XL voucher

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

