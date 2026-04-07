# MyInstagram

Instagram clone Android app built with Jetpack Compose, following MVP architecture pattern.

## Project Structure

```
com.example.myinstagram/
├── model/                  # Data models (signal fields for GUI Agent)
│   ├── User.kt            # Added isPrivate field
│   ├── Post.kt
│   ├── Comment.kt
│   ├── Story.kt
│   ├── Reel.kt
│   ├── Conversation.kt
│   ├── Message.kt
│   ├── MusicTrack.kt      # music track for post creation
│   └── LocationItem.kt    # location item for post creation
├── data/
│   └── DataRepository.kt  # Singleton data loader & mutator (+ addPost, addCommentToReel, etc.)
├── presenter/              # MVP Presenters
│   ├── HomePresenter.kt   # + follow, comment, like comment actions
│   ├── ReelsPresenter.kt  # + comment, share, menu, follow actions
│   ├── SearchPresenter.kt
│   ├── MessagesPresenter.kt # + sendMessage
│   ├── ProfilePresenter.kt
│   ├── OtherUserProfilePresenter.kt  # for viewing other users' profiles
│   ├── EditProfilePresenter.kt       # edit profile form state
│   ├── NotificationsPresenter.kt     # generates notifications from data
│   ├── NewPostPresenter.kt           # post creation: gallery + draft + sub-screen state
│   ├── SettingsPresenter.kt          # settings: saved, privacy, close friends, blocked
│   └── FollowersFollowingPresenter.kt # followers/following list management
├── ui/
│   ├── theme/              # Instagram light theme (inverted from dark)
│   ├── components/
│   │   ├── UserAvatar.kt   # Shared avatar component (loads from assets)
│   │   ├── ShareBottomSheet.kt    # share options bottom sheet
│   │   ├── MenuBottomSheet.kt     # post menu bottom sheet
│   │   └── CommentBottomSheet.kt  # comments bottom sheet
│   ├── navigation/
│   │   └── MainScreen.kt   # Bottom tab navigation + all screen routing
│   ├── home/
│   │   ├── HomeScreen.kt   # Home feed with + button, heart icon, avatar click
│   │   └── PostItem.kt     # Post with repost, share, follow, menu, comments
│   ├── reels/
│   │   └── ReelsScreen.kt  # Full-screen vertical pager with ExoPlayer + full interactions
│   ├── search/
│   │   └── SearchScreen.kt # Search bar + explore grid
│   ├── messages/
│   │   ├── MessagesScreen.kt    # DM conversation list
│   │   └── ChatDetailScreen.kt  # Chat detail page with message bubbles
│   ├── notifications/
│   │   └── NotificationsScreen.kt  # notifications page
│   ├── post/
│   │   ├── NewPostScreen.kt            # Router: dispatches to gallery/draft/sub-screens
│   │   ├── NewPostGalleryScreen.kt     # image gallery picker
│   │   ├── NewPostDraftScreen.kt       # post editing draft page
│   │   ├── NewPostVoteScreen.kt        # vote/poll creation
│   │   ├── NewPostMusicScreen.kt       # add music selection
│   │   ├── NewPostLocationScreen.kt    # add location selection
│   │   ├── NewPostAudienceScreen.kt    # audience selection
│   │   ├── NewPostShareToScreen.kt     # share to platforms toggles
│   │   └── NewPostMoreOptionsScreen.kt # hide likes, turn off comments
│   ├── profile/
│   │   ├── ProfileScreen.kt       # Current user profile + menu icon for settings
│   │   ├── OtherUserProfileScreen.kt  # other user profile (public/private)
│   │   ├── EditProfileScreen.kt       # edit profile form
│   │   ├── ShareProfileScreen.kt      # share profile with QR code
│   │   └── FollowersFollowingScreen.kt # followers/following list
│   └── settings/
│       ├── SettingsScreen.kt          # Settings & Activity router + main page
│       ├── SavedScreen.kt            # Saved posts grid + create collections
│       ├── TimeManagementScreen.kt   # Usage chart, daily limit, sleep mode
│       ├── AccountPrivacyScreen.kt   # Private account toggle
│       ├── CloseFriendsScreen.kt     # Add/remove close friends
│       └── BlockedScreen.kt          # Blocked users list + block dialog
└── MainActivity.kt
```

## Data Files

Mock data stored in `app/src/main/assets/data/`:
- `users.json` — 10 users (current user "Wang" + 9 others, some private)
- `posts.json` — 5 posts with likes, comments, saves
- `stories.json` — 6 stories
- `reels.json` — 3 reels
- `conversations.json` — 4 DM conversations
- `music.json` — 8 mock music tracks for post creation
- `locations.json` — 6 mock locations for post creation

## Assets

- `avatar/1.png` ~ `avatar/10.png` — User avatar images
- `image/1.jpeg` ~ `image/10.jpeg` — Post images
- `reels/1.mp4` ~ `reels/5.mp4` — Short video reels

## Signal Fields for GUI Agent Task Checking

| Task                  | Signal Field                  | Location        |
|-----------------------|-------------------------------|-----------------|
| Like a post           | `Post.likedBy: List<String>`  | posts.json      |
| Save/bookmark a post  | `Post.savedBy: List<String>`  | posts.json      |
| Comment on a post     | `Post.comments: List<Comment>`| posts.json      |
| Like a reel           | `Reel.likedBy: List<String>`  | reels.json      |
| Save a reel           | `Reel.savedBy: List<String>`  | reels.json      |
| Comment on a reel     | `Reel.comments: List<Comment>`| reels.json      |
| Send a message        | `Conversation.messages`       | conversations.json |
| Follow/unfollow       | `User.followers/following`    | users.json      |
| View a story          | `Story.viewedBy: List<String>`| stories.json    |
| Like a comment        | `Comment.likedBy: List<String>`| posts.json     |
| Create a new post     | `DataRepository.addPost()`    | posts.json      |

## Implemented Pages

1. **Home** — Top bar (+ new post button, Instagram logo centered, notifications heart icon), stories bar, suggested users, post feed with like/comment/share/save/repost/follow
2. **Search & Explore** — Search bar with user search, explore grid with real images
3. **Reels** — Full-screen vertical pager with ExoPlayer video playback (dark theme preserved), like/comment/share/save/menu/follow actions with CommentBottomSheet, ShareBottomSheet, MenuBottomSheet
4. **Direct Messages** — Conversation list with unread indicators, tap to open chat
5. **Chat Detail** — Message bubbles (left/right alignment), message input bar with send button, auto-scroll to latest
6. **User Profile (self)** — Avatar, stats (clickable followers/following counts), bio, edit/share buttons, menu icon for settings, posts/reels/tagged tabs with image grid
7. **Other User Profile** — View other users' profiles with Follow/Message buttons, public/private account support (lock icon for private)
8. **Edit Profile** — Form with Name, Username, Pronouns, Bio, Links, Gender fields
9. **Share Profile** — QR code card with username, share options
10. **Notifications** — Follow requests section, notification groups with likes, comments, follows
11. **New Post** — Multi-step post creation flow:
    - **Gallery Picker** — Select from available images with preview
    - **Draft Page** — Caption input, image preview (tap to enlarge), menu options for vote/hashtag/music/location/audience/share/more
    - **Vote/Poll** — Create poll with question and options (2-4)
    - **Add Music** — Search and select music tracks
    - **Add Location** — Search and select locations
    - **Audience** — Choose visibility: Everyone / Close Friends / Followers Except
    - **Share To** — Toggle sharing to Facebook, Threads, X
    - **More Options** — Toggle hide likes/views, turn off comments
12. **Followers/Following** — Tabbed list showing followers and following with remove/unfollow actions
13. **Settings & Activity** — Full settings page with sections: Your account, How you use Instagram, Who can see your content, How others can interact with you, What you see, Your app and media, For professionals, More info and support, Log in/out
    - **Saved** — View saved posts in grid, create new collections
    - **Time Management** — Weekly usage bar chart, daily time limit slider, sleep mode toggle
    - **Account Privacy** — Toggle private account on/off
    - **Close Friends** — Search and add/remove users to close friends list
    - **Blocked** — View blocked users, unblock, or block new users via dialog

## New Features (Latest Update)

- **Saved page** — View all saved posts in a grid, create new collections via + button dialog
- **Time Management page** — Weekly usage bar chart with daily averages, daily time limit slider (15-180 min), sleep mode toggle with schedule
- **Account Privacy page** — Toggle private account with descriptive text
- **Close Friends page** — Search users, add/remove from close friends list with Add/Remove buttons
- **Blocked page** — View blocked users with Unblock button, + button opens dialog to search and block new users
- **User model** — Added `closeFriends` and `blockedUsers` fields
- **DataRepository** — Added `getSavedPosts`, `getSavedReels`, `togglePrivacy`, `toggleCloseFriend`, `toggleBlockUser`
- **SettingsPresenter** — New presenter managing all settings state

## Build

```bash
./gradlew assembleDebug
```
