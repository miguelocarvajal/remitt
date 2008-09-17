#!/usr/bin/perl
#
#	$Id$
#	$Author$
#
# Package: Remitt::Session
#
#	Adaptation of the CGI::Session class to work with basic
#	authentication and XMLRPC::Lite for Remitt.
#

package Remitt::Session;

use CGI::Session;
use Data::Dumper;

# Method: new
#
#	Constructor
#
# Parameters:
#
# 	$id - Session id to be passed to the constructor.
#
# Returns:
#
# 	Remitt::Session object
#
sub new {
	my ($this, $id) = @_;
	$this->{id} = $id;
	return $this;
}

# Method: create
#
# 	Creates a new session on disk.
#
# Parameters:
#
# 	%param - Hash of parameters to be stored in the session.
#
sub create {
	my ($this, $param) = @_;

	$this->{session} = new CGI::Session(
		"driver:File",
		$this->{id},
		{Directory => '/tmp'});
	$this->{session}->expire('+1y');

	#print "\nparams to session are: ".Dumper($param)."\n";

	foreach my $k (keys %{$param}) {
		#print "added $k =  ".$param->{$k}." in session\n";
		$this->put($k, $param->{$k});
	}
	$this->{session}->param('authenticated', 1);
}

# Method: load
#
# 	Load previously initialized session data.
#
# Returns:
#
# 	Boolean, success.
#
sub load {
	my ($this) = @_;

	#print "load [ id = ".$this->{id}." ]\n";
	# Extend expiry so we know that we're still active
	$this->{session} = new CGI::Session(
		"driver:File",
		$this->{id},
		{Directory => '/tmp'});
	$this->{session}->expire('+1y');

	# If we did not serialize the proper value, no dice
	if (!$this->{session}->param('authenticated') eq 1) {
		#print "Could not authenticate in load()\n";
		return 0;
	} else {
		return 1;
	}
}

# Method: delete
#
# 	Destructor
#
sub delete {
	my ($this) = @_;
	$this->{session}->delete();
}

# Method: get
#
# 	Get stored value in session object.
#
# Parameters:
#
# 	$key - Key for value to lookup.
#
# Return:
#
# 	Specified value.
#
sub get { my ($this, $key) = @_; return $this->{session}->param($key); }

# Method: put
#
# 	Store key and value pair in session object.
#
# Parameters:
#
# 	$key - Key to associate with in the hash
#
# 	$val - Value to store
#
sub put { my ($this, $key, $val) = @_; $this->{session}->param($key, $val); }

1;
