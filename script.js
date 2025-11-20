let appState = {
  allPoses: [],
  allCategories: [],
  currentExercise: null,
  currentPoseIndex: 0,
  selectedDifficulty: 'beginner',
  timerInterval: null,
  timeRemaining: 0,
  exerciseStarted: false,
  exercisePaused: false,
  voiceEnabled: true,
  currentAudio: null,
  completedExercises: [],
  streak: 0,
  lastCompletedDate: null,
};

const poseDurations = {
  beginner: 0.5,
  intermediate: 1,
  advanced: 2
};

document.addEventListener('DOMContentLoaded', () => {
  Promise.all([
    fetch('data/poses.json').then(response => response.json()),
    fetch('data/categories.json').then(response => response.json())
  ])
  .then(([posesData, categoriesData]) => {
    appState.allPoses = posesData;
    appState.allCategories = categoriesData;
    appState.allCategories.forEach(category => {
      const categoryPoses = [];
      category.poses.forEach(pose => {
        const fullPose = appState.allPoses.find(p => p.id === pose);
        if (fullPose) {
          categoryPoses.push(fullPose);
        }
      });
      category.poses = categoryPoses;
    });
    appState.allPoses.forEach(pose => {
      pose.url_svg = `images/poses/${pose.id}.svg`;
    });
    displayPoses(appState.allPoses);
    displayCategories(appState.allCategories);
  })
  .catch(error => {
    console.error('Error fetching data:', error);
    const mainElement = document.querySelector('main');
    if (mainElement) {
      mainElement.innerHTML = '<p style="color: red; text-align: center;">Could not load yoga data. Please try refreshing the page.</p>';
    }
    const categoriesContainer = document.getElementById('categories-container');
    if (categoriesContainer) {
      categoriesContainer.innerHTML = '';
    }
    const posesContainer = document.getElementById('poses-container');
    if (posesContainer) {
      posesContainer.innerHTML = '';
    }
  });
  document.getElementById('start-btn').addEventListener('click', startExercise);
  document.getElementById('pause-btn').addEventListener('click', togglePause);
  document.getElementById('session-back-btn').addEventListener('click', closeExercise);
  const difficultyBtns = document.querySelectorAll('.difficulty-btn');
  difficultyBtns.forEach(btn => {
    btn.addEventListener('click', function() {
      appState.selectedDifficulty = this.dataset.level;
      difficultyBtns.forEach(b => b.classList.remove('selected'));
      this.classList.add('selected');
      if (appState.currentExercise) {
        const poseCount = document.querySelectorAll('.mini-pose').length;
        updateExerciseStats(appState.currentExercise.poses);
      }
    });
  });
  document.querySelector('[data-level="beginner"]').classList.add('selected');
  const voiceToggleButton = document.getElementById('voice-toggle-btn');
  voiceToggleButton.addEventListener('click', function() {
    appState.voiceEnabled = !appState.voiceEnabled;
    this.setAttribute('aria-pressed', appState.voiceEnabled.toString());
    const soundOnIcon = this.querySelector('.sound-on');
    const soundOffIcon = this.querySelector('.sound-off');
    if (appState.voiceEnabled) {
      soundOnIcon.style.display = 'block';
      soundOffIcon.style.display = 'none';
      if (appState.currentAudio) {
        appState.currentAudio.play();
      }
    } else {
      soundOnIcon.style.display = 'none';
      soundOffIcon.style.display = 'block';
      speechSynthesis.cancel();
      if (appState.currentAudio) {
        appState.currentAudio.pause();
      }
    }
  });
  const soundOnIcon = document.querySelector('.voice-toggle-btn .sound-on');
  const soundOffIcon = document.querySelector('.voice-toggle-btn .sound-off');
  if (appState.voiceEnabled) {
    soundOnIcon.style.display = 'block';
    soundOffIcon.style.display = 'none';
  } else {
    soundOnIcon.style.display = 'none';
    soundOffIcon.style.display = 'block';
  }
  document.getElementById('current-pose-img').loading = 'lazy';
  loadProgress();
});

function displayCategories(categories) {
  const categoriesContainer = document.getElementById('categories-container');
  categoriesContainer.innerHTML = '';
  const difficultyOrder = {
    'beginner': 1,
    'intermediate': 2,
    'advanced': 3
  };
  const sortedCategories = [...categories].sort((a, b) => {
    return difficultyOrder[a.difficulty.toLowerCase()] - difficultyOrder[b.difficulty.toLowerCase()];
  });
  sortedCategories.forEach(category => {
    const categoryCard = document.createElement('div');
    categoryCard.classList.add('category-card');
    const difficultyBadge = document.createElement('div');
    difficultyBadge.classList.add('difficulty-badge', `difficulty-${category.difficulty.toLowerCase()}`);
    difficultyBadge.textContent = category.difficulty;
    const title = document.createElement('h3');
    title.textContent = category.category_name;
    const description = document.createElement('div');
    description.classList.add('category-description');
    description.textContent = category.category_description || 'No description available';
    const posesPreview = document.createElement('div');
    posesPreview.classList.add('category-poses-preview');
    const maxThumbnails = Math.min(5, category.poses.length);
    for (let i = 0; i < maxThumbnails; i++) {
      const pose = category.poses[i];
      if (pose && pose.url_svg) {
        const thumbnail = document.createElement('img');
        thumbnail.src = pose.url_svg;
        thumbnail.alt = pose.english_name;
        thumbnail.loading = 'lazy';
        thumbnail.classList.add('category-pose-thumbnail');
        posesPreview.appendChild(thumbnail);
      }
    }
    const stats = document.createElement('div');
    stats.classList.add('category-stats');
    stats.textContent = `${category.poses.length} poses`;

    if (appState.completedExercises.includes(category.id)) {
      const completedIcon = document.createElement('div');
      completedIcon.classList.add('completed-icon');
      completedIcon.textContent = '✔';
      categoryCard.appendChild(completedIcon);
    }

    categoryCard.appendChild(difficultyBadge);
    categoryCard.appendChild(title);
    categoryCard.appendChild(description);
    categoryCard.appendChild(posesPreview);
    categoryCard.appendChild(stats);
    categoryCard.setAttribute('tabindex', '0');
    categoryCard.addEventListener('click', () => {
      openExercise(category);
    });
    categoryCard.addEventListener('keydown', function(event) {
      if (event.key === 'Enter' || event.key === ' ') {
        event.preventDefault();
        openExercise(category);
      }
    });
    categoriesContainer.appendChild(categoryCard);
  });
}

function openExercise(category) {
  appState.currentExercise = category;
  document.getElementById('exercise-title').textContent = `${category.category_name} (${category.difficulty})`;
  document.getElementById('exercise-description').textContent = category.category_description;
  document.getElementById('session-fullscreen').style.display = 'flex';
  document.body.style.overflow = 'hidden';
  appState.currentPoseIndex = 0;
  appState.exerciseStarted = false;
  appState.exercisePaused = false;
  document.getElementById('start-btn').textContent = 'Start Exercise';
  document.getElementById('start-btn').style.display = 'block';
  document.getElementById('pause-btn').style.display = 'none';
  document.getElementById('difficulty-selector').style.display = 'block';
  clearInterval(appState.timerInterval);
  document.getElementById('timer').textContent = '00:00';
  document.getElementById('timer-label').textContent = 'Get ready...';
  document.getElementById('progress-bar').style.width = '0%';
  const allPosesPreview = document.getElementById('all-poses-preview');
  allPosesPreview.innerHTML = '';
  appState.currentExercise.poses.forEach((pose, index) => {
    const miniImg = document.createElement('img');
    miniImg.src = pose.url_svg;
    miniImg.alt = pose.english_name;
    miniImg.loading = 'lazy';
    miniImg.classList.add('mini-pose');
    if (index === 0) miniImg.classList.add('active');
    allPosesPreview.appendChild(miniImg);
  });
  updateExerciseStats(category.poses);
  const currentPose = category.poses[0];
  document.getElementById('current-pose-img').src = currentPose.url_svg;
  document.getElementById('current-pose-name').textContent = currentPose.english_name;
  document.querySelector('.current-pose').style.display = 'none';
  history.pushState({
    action: 'openExercise'
  }, document.title);
}

window.addEventListener('popstate', (event) => {
  if (document.getElementById('session-fullscreen').style.display === 'flex') {
    closeExercise();
  }
});

function startExercise() {
  if (appState.exerciseStarted) {
    return;
  }
  appState.exerciseStarted = true;
  appState.exercisePaused = false;
  document.getElementById('pause-btn').textContent = 'Pause';
  document.getElementById('start-btn').style.display = 'none';
  document.getElementById('difficulty-selector').style.display = 'none';
  document.getElementById('exercise-title').style.display = 'none';
  document.getElementById('exercise-description').style.display = 'none';
  document.getElementById('exercise-stats').style.display = 'none';
  document.getElementById('pause-btn').style.display = 'inline-block';
  document.querySelector('.current-pose').style.display = 'block';
  const categoryPoses = [];
  appState.currentExercise.poses.forEach(pose => {
    const fullPose = appState.allPoses.find(p => p.id === pose.id);
    if (fullPose) {
      categoryPoses.push(fullPose);
    }
  });
  appState.currentPoseIndex = 0;
  startPoseTimer(categoryPoses);
}

function startPoseTimer(poses) {
  if (appState.currentPoseIndex >= poses.length) {
    finishExercise();
    return;
  }
  const currentPose = poses[appState.currentPoseIndex];
  document.getElementById('current-pose-img').src = currentPose.url_svg;
  document.getElementById('current-pose-name').textContent = currentPose.english_name;
  const miniPoses = document.querySelectorAll('.mini-pose');
  miniPoses.forEach((pose, index) => {
    if (index === appState.currentPoseIndex) {
      pose.classList.add('active');
    } else {
      pose.classList.remove('active');
    }
  });
  if (appState.voiceEnabled && currentPose.pose_description) {
    speakPoseDescription(currentPose.pose_description);
  }
  appState.timeRemaining = currentPose.base_time * poseDurations[appState.selectedDifficulty];
  updateTimerDisplay();
  const progressPercent = (appState.currentPoseIndex / poses.length) * 100;
  document.getElementById('progress-bar').style.width = `${progressPercent}%`;
  appState.timerInterval = setInterval(() => {
    if (appState.exercisePaused) return;
    appState.timeRemaining--;
    updateTimerDisplay();
    if (appState.timeRemaining <= 0) {
      clearInterval(appState.timerInterval);
      appState.currentPoseIndex++;
      setTimeout(() => startPoseTimer(poses), 1000);
    }
  }, 1000);
}

function updateTimerDisplay() {
  const minutes = Math.max(0, Math.floor(appState.timeRemaining / 60));
  const seconds = Math.max(0, (appState.timeRemaining % 60).toFixed(0));
  document.getElementById('timer').textContent = `${minutes.toString().padStart(2,'0')}:${seconds.toString().padStart(2,'0')}`;
  if (appState.timeRemaining <= 5) {
    document.getElementById('timer-label').textContent = 'Get ready for next pose...';
  } else {
    document.getElementById('timer-label').textContent = 'Hold pose';
  }
}

function togglePause() {
  appState.exercisePaused = !appState.exercisePaused;
  document.getElementById('pause-btn').textContent = appState.exercisePaused ? 'Resume' : 'Pause';
  if (appState.currentAudio && appState.voiceEnabled) {
    if (appState.exercisePaused) {
      appState.currentAudio.pause();
    } else {
      appState.currentAudio.play();
    }
  }
}

function finishExercise() {
  document.getElementById('current-pose-img').src = '';
  document.getElementById('current-pose-name').textContent = 'Exercise Completed!';
  document.getElementById('timer').textContent = '00:00';
  document.getElementById('timer-label').textContent = 'Completed';
  document.getElementById('progress-bar').style.width = '100%';
  document.getElementById('pause-btn').style.display = 'none';
  document.getElementById('start-btn').style.display = 'inline-block';
  document.getElementById('start-btn').textContent = 'Restart Exercise';
  appState.exerciseStarted = false;
  document.querySelector('.current-pose').style.display = 'block';
  document.getElementById('exercise-title').style.display = 'block';
  document.getElementById('exercise-stats').style.display = 'block';

  const today = new Date().toDateString();
  const lastDate = appState.lastCompletedDate ? new Date(appState.lastCompletedDate).toDateString() : null;

  if (today !== lastDate) {
    const yesterday = new Date();
    yesterday.setDate(yesterday.getDate() - 1);
    const yesterdayDate = yesterday.toDateString();

    if (lastDate === yesterdayDate) {
      appState.streak++;
    } else {
      appState.streak = 1;
    }
    appState.lastCompletedDate = new Date().toISOString();
  }

  if (!appState.completedExercises.includes(appState.currentExercise.id)) {
    appState.completedExercises.push(appState.currentExercise.id);
  }

  saveProgress();
  updateUI();
}

function closeExercise() {
  document.getElementById('session-fullscreen').style.display = 'none';
  document.body.style.overflow = '';
  clearInterval(appState.timerInterval);
  appState.exerciseStarted = false;
  document.getElementById('exercise-title').style.display = 'block';
  document.getElementById('exercise-description').style.display = 'block';
  document.getElementById('exercise-stats').style.display = 'block';
  if (speechSynthesis.speaking) {
    speechSynthesis.cancel();
  }
  if (appState.currentAudio) {
    appState.currentAudio.pause();
  }
}

function updateExerciseStats(poses) {
  var totalDuration = 0;
  poses.forEach(pose => {
    totalDuration += pose.base_time * poseDurations[appState.selectedDifficulty];
  });
  const minutes = Math.floor(totalDuration / 60);
  const seconds = totalDuration % 60;
  document.getElementById('exercise-stats').textContent = `Total: ${poses.length} poses • ${minutes}m ${seconds}s`;
}

function displayPoses(poses) {
  const posesContainer = document.getElementById('poses-container');
  posesContainer.innerHTML = '';
  const difficultyOrder = {
    'beginner': 1,
    'intermediate': 2,
    'advanced': 3
  };
  const sortedPoses = [...poses].sort((a, b) => {
    return difficultyOrder[a.difficulty.toLowerCase()] - difficultyOrder[b.difficulty.toLowerCase()];
  });
  sortedPoses.forEach(pose => {
    const poseCard = document.createElement('div');
    poseCard.classList.add('pose-card');
    poseCard.innerHTML = `
            <div class="difficulty-badge difficulty-${pose.difficulty.toLowerCase()}">${pose.difficulty}</div>
            <img src="${pose.url_svg}" alt="${pose.english_name}" loading="lazy" width="200" height="200">
            <h2>${pose.english_name}</h2>
            <p><strong>Benefits:</strong> ${pose.pose_benefits}</p>
        `;
    posesContainer.appendChild(poseCard);
  });
}

const speakPoseDescription = (text) => {
  if (!appState.voiceEnabled || !text) return;
  const currentPose = appState.currentExercise.poses[appState.currentPoseIndex];
  const audioFile = `data/audio/${currentPose.id}.mp3`;
  if (appState.currentAudio) {
    appState.currentAudio.pause();
  }
  appState.currentAudio = new Audio(audioFile);
  appState.currentAudio.onerror = () => {
    console.log(`Audio file not found: ${audioFile}, falling back to speech synthesis`);
    const utterance = new SpeechSynthesisUtterance(text);
    const voices = speechSynthesis.getVoices();
    const calmVoice = voices.find(voice => voice.name.includes('Samantha') || voice.name.includes('Karen') || (voice.name.includes('Female') && voice.lang.includes('en')));
    if (calmVoice) utterance.voice = calmVoice;
    utterance.rate = 0.85;
    utterance.pitch = 0.9;
    speechSynthesis.cancel();
    speechSynthesis.speak(utterance);
  };
  appState.currentAudio.oncanplaythrough = () => {
    console.log(`Playing audio file: ${audioFile}`);
    appState.currentAudio.play();
  };
  appState.currentAudio.load();
};

function saveProgress() {
  const progress = {
    completedExercises: appState.completedExercises,
    streak: appState.streak,
    lastCompletedDate: appState.lastCompletedDate,
  };
  localStorage.setItem('yogaBaseProgress', JSON.stringify(progress));
}

function loadProgress() {
  const savedProgress = localStorage.getItem('yogaBaseProgress');
  if (savedProgress) {
    try {
      const progress = JSON.parse(savedProgress);
      appState.completedExercises = progress.completedExercises || [];
      appState.streak = progress.streak || 0;
      appState.lastCompletedDate = progress.lastCompletedDate;
      updateStreak();
      updateUI();
    } catch (error) {
      console.error('Error parsing yogaBaseProgress from localStorage:', error);
      localStorage.removeItem('yogaBaseProgress');
    }
  }
}

function updateStreak() {
  if (!appState.lastCompletedDate) {
    appState.streak = 0;
    return;
  }

  const today = new Date().toDateString();
  const lastDate = new Date(appState.lastCompletedDate).toDateString();

  if (today === lastDate) {
    // Already completed a session today, streak is maintained.
    return;
  }

  const yesterday = new Date();
  yesterday.setDate(yesterday.getDate() - 1);
  const yesterdayDate = yesterday.toDateString();

  if (lastDate !== yesterdayDate) {
    // Last session was not yesterday, reset streak.
    appState.streak = 0;
    saveProgress();
  }
}

function updateUI() {
  const streakCount = appState.streak;
  const streakText = `${streakCount} ${streakCount === 1 ? 'day' : 'days'}`;
  document.getElementById('streak-count').textContent = streakText;
  displayCategories(appState.allCategories);
}
