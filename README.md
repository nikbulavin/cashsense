[<img alt="Get it on Google Play" src="docs/images/badges/google-play-badge.png" height="70"/>](https://play.google.com/store/apps/details?id=ru.resodostudios.cashsense)

Cash Sense
==========

It's a mobile app designed to help users manage their finances effectively.
Whether you want to create wallets in different currencies, set up categories for transactions, or
track subscription payment dates, Cash Sense has you covered.

# Features

- **Wallets** – easily create multiple wallets with support for various currencies. Keep your
  finances organized by managing different currencies in separate wallets.

- **Categories** – categorize your transactions for a clearer overview of your spending patterns.
  Customize categories based on your unique financial needs.

- **Subscriptions** – never miss a payment again. Set up and track subscription payments to stay on
  top of your financial commitments.

## Screenshots

![Screenshots](docs/images/screenshots.png "Screenshots")

# UI

The app was created following [Material 3][m3] guidelines. All screens and UI components are built entirely using [Jetpack Compose][compose].

### Themes

- **Dynamic color** – adapts to the [user's current color scheme][m3colorSystem] (Android 12+).
- **Default theme** – uses predefined colors when dynamic color is not available.

Both themes also include support for dark mode
and [three levels of contrast][m3contrast].

### Animations

- **[Lottie][lottie]**
- **[Shared element transition][composeSharedElements]**

# Localization

If you'd like to contribute to translating this app, you can [join][crowdinInvite] our localization efforts. 
Your contributions will help ensure that everyone can enjoy the app in their native language.

# Donate

| Cryptocurrency     | Address                                            |
|--------------------|----------------------------------------------------|
| **Bitcoin (BTC)**  | `bc1qn2dd85ek6dm8mm3wu3ws6cq507zgrtlgatl20z`       |
| **Ethereum (ETH)** | `0x78cD353134CbffeD8B941fF05a3Ac8B0bBd308e6`       |
| **USDT (TRC20)**   | `TNs7AvHQ2TjFDKMA7nJ1US31A57tRUMvqN`               |
| **Toncoin (TON)**  | `UQA9a2q2RnLsSbJKdrKTAQcMrGnYVXV31CRgHCTZ8706mEfT` |
| **Litecoin (LTC)** | `ltc1q8qrcnc6y3ajg7xslaxy6n90d8hk3252amzq8aw`      |

# License

**Cash Sense** is distributed under the terms of the Apache License (Version 2.0). See the
[license](LICENSE) for more information.

[m3]: https://m3.material.io/

[m3contrast]: https://m3.material.io/styles/color/system/how-the-system-works#0207ef40-7f0d-4da8-9280-f062aa6b3e04

[m3colorSystem]: https://m3.material.io/styles/color/system/how-the-system-works#da0abfef-1503-477d-a3d7-9378b4a9948e

[compose]: https://developer.android.com/jetpack/compose

[composeSharedElements]: https://developer.android.com/develop/ui/compose/animation/shared-elements

[lottie]: https://github.com/airbnb/lottie/blob/master/android-compose.md

[crowdinInvite]: https://crowdin.com/project/cashsense/invite?h=d573fbd44b6bcf6bfc0dfbbfb3bf800f2198579
