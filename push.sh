setup_git() {
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "Travis CI"
}

commit_files() {
  git add .
  git commit --message "Travis build: $TRAVIS_BUILD_NUMBER"
}

upload_files() {
  git remote add origin https://${GITHUB_API_KEY}@github.com/avisoftware/ray-tracing.git > /dev/null 2>&1
  git push --quiet --set-upstream origin master
}

setup_git
commit_files
upload_files

