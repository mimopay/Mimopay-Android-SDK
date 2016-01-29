Mimopay Android SDK release notes
================================

v3.2.3
-------
* additional return info, at index-4, payment submit stage
* fix layout
* add denom amount. denomAmount just for show, but keep submit denomValue
* add disclaimer info from backend
* fix error message sentences. remove the extra technical info like: length=0 index=0
* fix bug on enableGateway on the first initialization
* dynamic UP. now can be updated on backend

3.2.2
------
* fix dpoint on receiving sms shortcode
* add new payment method indosat airtime

3.2.1
------
* fix minor bug on tsel upoint

3.2
------
* re-improve UI look. all logo not used anymore
* improved speed
* fix minor bug on vietnam telco payment method

3.1
------
* improve UI look
* remove ErrorChannelIsNotReady, actually it is maintenance mode

3.0
------
* add new payment method, Vietnam telco, VnTelco
* improve UI look
* add new error, ErrorChannelIsNotReady, issued when channelStatus = 0

2.9.2
------
* add new function, executeUPointAirtime(String amount, String phoneNumber, boolean autosendsms, String upointItem)

2.9.1
------
* make sure it use https on production

2.9
------
* new payment method, upoint voucher
* new payment method, celcom airtime
* add fix amount for all airtime payment
* add atm also with fix amount
* bug fix, exclude 10% on all non-upoint payment methods

2.8.1
------
* minor change on displaying fixed denom on airtime and atm

2.8
------
* bug fixed, ErrorInvalidPhoneNumber raised because of the total digit input of phone number
* for digi, it allow to use country code however still should not use any non-numeric character such as '+' character

2.7
------
* allow dev to get the http status code or java exception status
* bug fix on generating APIkey on the final URL
* add 2 functions for complete payment for Digi (DPoint)
* add filter on productName field to have safe URL

2.6
------
* add new function: executeUPointAirtime(String amount)
* add new function: executeXLAirtime(String amount)

2.5
------
* standardize all onReturn 'info' and 'params'. Please refer to the document

2.4
------
* fix minor bug, on topup payment methods return

2.3
------
* now suppor custom language, see CustomLang.java
* add mpoint airtime payment method (maxis)
* add dpoint airtime payment method (digi)
* several minor bug fixed

2.2
------
* on auto SMS send, LANJUT -> Kirim SMS
* support dual sim, by switch to stock message app, upoint & XL
* minor bug fix on XL airtime
* Last used phone number, user no need to re-type. It remember the last number type
* auto disable log when switch to gateway (production)

2.1
------
* add some error handling for invalid phone number
* normalizing value of amount

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

