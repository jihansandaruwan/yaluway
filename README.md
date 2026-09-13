# යාළුway (Yaluway)

SE4041 – Mobile Application Design and Development  
Assignment 01 – Community Services and Local Marketplace

**Student:** Sandaruwan W J  
**Registration number:** IT22294548

Help nearby. Trade nearby. Trust nearby.

## What it is

A Kotlin Android app for neighbours to post **Help**, **Services**, and **Marketplace** listings (lend, borrow, donate, or free) in their Sri Lankan city. Accounts, posts, saved items, and requests are stored locally with Room (SQLite).

## Screens

- Splash, login, and register (live red validation)
- 3-page onboarding slideshow
- Home feed with search, category tabs, and nearby filter
- Create / edit / delete a post (photo + 10-digit phone)
- Post detail, save, and send request
- My posts, saved items, and profile
- Incoming / sent requests (Accept shares the owner phone)

## Room tables (`yaluway.db`)

| Table | Purpose |
| --- | --- |
| `users` | Account, city, and avatar path |
| `posts` | Help / Services / Marketplace listings |
| `saved_posts` | Posts a user bookmarked |
| `requests` | PENDING / ACCEPTED / DECLINED |

Inspect them in Android Studio: **App Inspection → Database Inspector** while the app is running.

## Open in Android Studio

1. Open folder `Assigment 01` (this repository), not a parent tutorial folder.
2. Wait for Gradle sync.
3. Run on an emulator or a phone with USB debugging.

## Viva demo

1. Register an account (example city: Negombo).
2. Create a Marketplace post with a photo and a 10-digit phone number.
3. Log out and register a second account in the same city.
4. Send a request on the first post, then log back in as the owner and Accept.
5. Log in with a Kandy account to show the nearby feed change.
