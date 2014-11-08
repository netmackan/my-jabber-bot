#!/bin/bash
#
# Subversion post-commit hook posting to Jabberbot.
#
# Installation:
# cd SVNREPO
# cp THISFILE SVNREPO/hooks/
# cp hooks/post-commit.tmpl hooks/post-commit
# Edit hooks/post-commit
# Uncomment the "$REPOS"/hooks/mailer.py line and instead add:
# "$REPOS"/hooks/jabberbot-svn-post-commit.sh commit "$REPOS" $REV "$REPOS"/jabberbot-svn-post-commit.conf

# TODO: Check arguments

REPOS=$2
REV=$3
CONF=$4

REPONAME=$(basename "$REPOS")
AUTHOR=`svnlook author $REPOS -r $REV`
CHANGES=`svnlook changed $REPOS -r $REV`
MESSAGE=`svnlook log $REPOS -r $REV`

MSGDIR="/tmp/commit-messages/$REPONAME"

mkdir -p "$MSGDIR"
chmod a+rw "$MSGDIR"
FILE=$MSGDIR/commit-$REV-`date +%s`.msg

cat << _EOF_ > ${FILE}
${AUTHOR} commited r${REV} in ${REPONAME}:
${MESSAGE}
${CHANGES}
_EOF_

chmod a+rw ${FILE}
