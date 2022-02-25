# GoogleSigninIssue
Sample app to demonstarte the issue where we are getting an expired id token when using Google signin. This is not reproducible for every user, but some user who get the expired token, will always received the same expired token.

## Workaround
- Signout of google account on the device and sign back in to the same Google account. Then open the app and try Google Sigin. This way we reeive new token.
