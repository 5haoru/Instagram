package com.example.myinstagram.ui.post

import androidx.compose.runtime.*
import com.example.myinstagram.presenter.NewPostPresenter
import com.example.myinstagram.presenter.NewPostStep

@Composable
fun NewPostScreen(
    presenter: NewPostPresenter,
    onBack: () -> Unit,
    onShowToast: (String) -> Unit = {}
) {
    LaunchedEffect(Unit) { presenter.loadData() }

    when (presenter.currentStep) {
        NewPostStep.GALLERY -> NewPostGalleryScreen(
            presenter = presenter,
            onBack = {
                presenter.resetDraft()
                onBack()
            },
            onNext = { presenter.onNextFromGallery() }
        )
        NewPostStep.DRAFT -> NewPostDraftScreen(
            presenter = presenter,
            onBack = { presenter.currentStep = NewPostStep.GALLERY },
            onShare = {
                presenter.sharePost()
                onShowToast("Post shared!")
                presenter.resetDraft()
                onBack()
            }
        )
        NewPostStep.VOTE -> NewPostVoteScreen(
            presenter = presenter,
            onBack = { presenter.currentStep = NewPostStep.DRAFT }
        )
        NewPostStep.MUSIC -> NewPostMusicScreen(
            presenter = presenter,
            onBack = { presenter.currentStep = NewPostStep.DRAFT }
        )
        NewPostStep.LOCATION -> NewPostLocationScreen(
            presenter = presenter,
            onBack = { presenter.currentStep = NewPostStep.DRAFT }
        )
        NewPostStep.AUDIENCE -> NewPostAudienceScreen(
            presenter = presenter,
            onBack = { presenter.currentStep = NewPostStep.DRAFT }
        )
        NewPostStep.SHARE_TO -> NewPostShareToScreen(
            presenter = presenter,
            onBack = { presenter.currentStep = NewPostStep.DRAFT }
        )
        NewPostStep.MORE_OPTIONS -> NewPostMoreOptionsScreen(
            presenter = presenter,
            onBack = { presenter.currentStep = NewPostStep.DRAFT }
        )
    }
}
